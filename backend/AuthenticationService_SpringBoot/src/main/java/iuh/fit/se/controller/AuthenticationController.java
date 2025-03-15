package iuh.fit.se.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuthException;
import com.nimbusds.jose.JOSEException;

import iuh.fit.se.dto.request.*;
import iuh.fit.se.dto.response.ApiResponse;
import iuh.fit.se.dto.response.AuthenticationResponse;
import iuh.fit.se.dto.response.IntrospectResponse;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Xác thực ID Token từ Google và trả về JWT
    @PostMapping("/login-google")
    public ApiResponse<AuthenticationResponse> loginWithGoogle(@RequestBody TokenRequest tokenRequest)
            throws FirebaseAuthException {
        AuthenticationResponse response = authenticationService.verifyGoogleToken(tokenRequest);
        return ApiResponse.<AuthenticationResponse>builder().result(response).build();
    }

    @PostMapping("/login-email-password")
    public ApiResponse<AuthenticationResponse> loginWithEmailPassword(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.loginWithEmailPassword(request);
        return ApiResponse.<AuthenticationResponse>builder().result(response).build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        AuthenticationResponse response = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(response).build();
    }

    // Xác thực JWT và trả về thông tin người dùng
    @GetMapping("/verify-jwt")
    public ApiResponse<UserResponse> verifyJwtToken(@RequestHeader("Authorization") String token) {
        UserResponse userResponse = authenticationService.verifyJwtToken(token.replace("Bearer ", ""));
        return ApiResponse.<UserResponse>builder().result(userResponse).build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(
            @RequestHeader("Authorization") String token, @RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(token.replace("Bearer ", ""), request);
        return ApiResponse.<String>builder()
                .result("Password changed successfully")
                .build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @PostMapping("/register")
    public ApiResponse<AuthenticationResponse> register(@RequestBody AuthenticationRegisterRequest request) {
        log.warn(request.toString());
        AuthenticationResponse response = authenticationService.register(request);
        return ApiResponse.<AuthenticationResponse>builder().result(response).build();
    }
}
