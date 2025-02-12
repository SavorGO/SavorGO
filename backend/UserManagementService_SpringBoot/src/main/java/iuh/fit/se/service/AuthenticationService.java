package iuh.fit.se.service;

import com.google.firebase.auth.FirebaseAuthException;

import iuh.fit.se.dto.request.AuthenticationRequest;
import iuh.fit.se.dto.request.ChangePasswordRequest;
import iuh.fit.se.dto.request.TokenRequest;
import iuh.fit.se.dto.response.AuthenticationResponse;
import iuh.fit.se.dto.response.UserResponse;

public interface AuthenticationService {
    AuthenticationResponse verifyGoogleToken(TokenRequest tokenRequest) throws FirebaseAuthException;
    UserResponse verifyJwtToken(String jwtToken);
	AuthenticationResponse loginWithEmailPassword(AuthenticationRequest request);
	void changePassword(String jwtToken, ChangePasswordRequest request);
}
