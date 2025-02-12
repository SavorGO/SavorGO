package iuh.fit.se.controller;

import com.google.firebase.auth.FirebaseAuthException;

import iuh.fit.se.dto.request.AuthenticationRequest;
import iuh.fit.se.dto.request.TokenRequest;
import iuh.fit.se.dto.response.AuthenticationResponse;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Xác thực ID Token từ Google và trả về JWT
    @PostMapping("/login-google")
    public ResponseEntity<AuthenticationResponse> loginWithGoogle(@RequestBody TokenRequest tokenRequest) throws FirebaseAuthException {
        AuthenticationResponse response = authenticationService.verifyGoogleToken(tokenRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login-email-password")
    public ResponseEntity<AuthenticationResponse> loginWithEmailPassword(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.loginWithEmailPassword(request);
        return ResponseEntity.ok(response);
    }


    // Xác thực JWT và trả về thông tin người dùng
    @GetMapping("/verify-jwt")
    public ResponseEntity<UserResponse> verifyJwtToken(@RequestHeader("Authorization") String token) {
        UserResponse userResponse = authenticationService.verifyJwtToken(token.replace("Bearer ", ""));
        return ResponseEntity.ok(userResponse);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }
}
