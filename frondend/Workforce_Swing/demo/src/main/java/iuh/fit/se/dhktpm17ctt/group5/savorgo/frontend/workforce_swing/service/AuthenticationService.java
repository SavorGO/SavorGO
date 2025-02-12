package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import okhttp3.Response;
import java.io.IOException;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;

public interface AuthenticationService {
	User verifyJwtToken(String jwtToken) throws IOException;

	String loginWithGoogle(String idToken) throws IOException;

	String loginWithEmailPassword(String email, String password) throws IOException;
}
