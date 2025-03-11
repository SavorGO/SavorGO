package iuh.fit.se.service;

import java.text.ParseException;

import com.google.firebase.auth.FirebaseAuthException;
import com.nimbusds.jose.JOSEException;

import iuh.fit.se.dto.request.*;
import iuh.fit.se.dto.response.AuthenticationResponse;
import iuh.fit.se.dto.response.IntrospectResponse;
import iuh.fit.se.dto.response.UserResponse;

public interface AuthenticationService {
    AuthenticationResponse verifyGoogleToken(TokenRequest tokenRequest) throws FirebaseAuthException;

    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;

    void logout(LogoutRequest request) throws ParseException, JOSEException;

    UserResponse verifyJwtToken(String jwtToken);

    AuthenticationResponse loginWithEmailPassword(AuthenticationRequest request);

    void changePassword(String jwtToken, ChangePasswordRequest request);

    AuthenticationResponse register(AuthenticationRegisterRequest request);
}
