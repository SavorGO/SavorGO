package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.IOException;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.AuthenticationService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.AuthenticationServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.TokenManager;
import raven.modal.Toast;

public class AuthenticationController {
    private AuthenticationService authenticationService = new AuthenticationServiceImpl();
    
	public String loginWithEmailPassword(String email, String password) throws IOException {
		return authenticationService.loginWithEmailPassword(email, password);
	}
    
    public String loginWithGoogle(String idToken) throws IOException {
        return authenticationService.loginWithGoogle(idToken);
    }
    
    public User verifyJwtToken() throws Exception {
        // Đọc token đã được mã hóa từ file
        String encryptedToken = TokenManager.readEncryptedTokenFromFile();

        if (encryptedToken == null || encryptedToken.isEmpty()) {
            return null;
        }

        // Giải mã token
        String decryptedToken = null;
			decryptedToken = TokenManager.decryptToken(encryptedToken);

        // Gọi phương thức verifyJwtToken từ AuthenticationService để xác thực token đã giải mã
			return authenticationService.verifyJwtToken(decryptedToken);
    }

}