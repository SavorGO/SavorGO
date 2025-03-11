package iuh.fit.se.service.impl;

import java.text.ParseException;
import java.util.Date;

import iuh.fit.se.entity.InvalidatedToken;
import iuh.fit.se.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import iuh.fit.se.dto.request.*;
import iuh.fit.se.dto.response.AuthenticationResponse;
import iuh.fit.se.dto.response.IntrospectResponse;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.entity.User;
import iuh.fit.se.exception.AppException;
import iuh.fit.se.exception.ErrorCode;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.AuthenticationService;
import iuh.fit.se.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    // Xác thực ID Token từ Google, tạo JWT và trả về AuthenticationResponse
    @Override
    public AuthenticationResponse verifyGoogleToken(TokenRequest tokenRequest) throws FirebaseAuthException {
        String idToken = tokenRequest.getToken();

        // Xác minh ID Token với Firebase
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

        // Lấy email từ token
        String email = decodedToken.getEmail();

        // Kiểm tra người dùng trong database
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tạo JWT từ user
        String jwtToken = jwtUtil.generateToken(user);

        return new AuthenticationResponse(jwtToken, "Login successful");
    }

    @Override
    public AuthenticationResponse loginWithEmailPassword(AuthenticationRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Tìm user trong database
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Tạo JWT token
        String jwtToken = jwtUtil.generateToken(user);

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .message("Login successful")
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean valid = true;
        try {
            verifyToken(token);
        }catch (AppException e){
            valid = false;
        }
        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(jwtUtil.getSecretKey());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!verified && expityTime.after(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }

    // Xác thực JWT và trả về thông tin người dùng
    @Override
    public UserResponse verifyJwtToken(String jwtToken) {
        String email = jwtUtil.extractEmail(jwtToken);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return objectMapper.convertValue(user, UserResponse.class);
    }

    @Override
    public void changePassword(String jwtToken, ChangePasswordRequest request) {
        // Lấy email từ token JWT
        String email = jwtUtil.extractEmail(jwtToken);

        // Tìm người dùng trong database
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse register(AuthenticationRegisterRequest request) {
        // Check if the user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXITS);
        }

        // Create a new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
//        user.setAddress(request.getAddress());

        // Save the user to the database
        userRepository.save(user);

        // Generate JWT token
        String jwtToken = jwtUtil.generateToken(user);

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .message("Registration successful")
                .build();
    }
}
