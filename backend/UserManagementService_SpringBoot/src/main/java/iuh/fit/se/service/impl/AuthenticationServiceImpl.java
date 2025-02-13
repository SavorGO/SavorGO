package iuh.fit.se.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import iuh.fit.se.dto.request.AuthenticationRequest;
import iuh.fit.se.dto.request.ChangePasswordRequest;
import iuh.fit.se.dto.request.TokenRequest;
import iuh.fit.se.dto.response.AuthenticationResponse;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.entity.User;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.AuthenticationService;
import iuh.fit.se.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Xác thực ID Token từ Google, tạo JWT và trả về AuthenticationResponse
    @Override
    public AuthenticationResponse verifyGoogleToken(TokenRequest tokenRequest) throws FirebaseAuthException {
        String idToken = tokenRequest.getToken();

        // Xác minh ID Token với Firebase
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

        // Lấy email từ token
        String email = decodedToken.getEmail();

        // Kiểm tra người dùng trong database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

        // Tạo JWT từ user
        String jwtToken = jwtUtil.generateToken(user);

        return new AuthenticationResponse(jwtToken, "Login successful");
    }
    
    @Override
    public AuthenticationResponse loginWithEmailPassword(AuthenticationRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // Tìm user trong database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Tạo JWT token
        String jwtToken = jwtUtil.generateToken(user);

        return new AuthenticationResponse(jwtToken, "Login successful");
    }

    // Xác thực JWT và trả về thông tin người dùng
    @Override
    public UserResponse verifyJwtToken(String jwtToken) {
        String email = jwtUtil.extractEmail(jwtToken);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return objectMapper.convertValue(user, UserResponse.class);
    }
    @Override
    public void changePassword(String jwtToken, ChangePasswordRequest request) {
        // Lấy email từ token JWT
        String email = jwtUtil.extractEmail(jwtToken);
        
        // Tìm người dùng trong database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Old password is incorrect");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
