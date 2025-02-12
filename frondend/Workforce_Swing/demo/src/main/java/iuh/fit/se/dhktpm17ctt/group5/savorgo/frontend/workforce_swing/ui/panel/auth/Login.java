package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.auth;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.Demo;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.AuthenticationController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.menu.MyDrawerBuilder;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.Form;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpServerHandler;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.TokenManager;
import net.miginfocom.swing.MigLayout;
import raven.modal.Drawer;
import raven.modal.Toast;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.swing.*;
import com.sun.net.httpserver.HttpExchange;

public class Login extends Form {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chRememberMe;
    private JButton cmdLogin;
    private JButton cmdLoginGoogle;
    private AuthenticationController authenticationController = new AuthenticationController();

    public Login() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        chRememberMe = new JCheckBox("Remember me");
        cmdLogin = new JButton("Login");
        cmdLoginGoogle = new JButton("Login with Google");

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 35 45", "fill,250:280"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:shade($Panel.background,5%);" +
                "[dark]background:tint($Panel.background,5%);");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:shade($Panel.background,10%);" +
                "[dark]background:tint($Panel.background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel description = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:$Label.disabledForeground;");

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Username"), "gapy 8");
        panel.add(txtEmail);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(chRememberMe, "grow 0");
        panel.add(cmdLogin, "gapy 10");
        panel.add(cmdLoginGoogle, "gapy 10");
        add(panel);

        // Event listener for standard login button
        cmdLogin.addActionListener((e) -> {
          loginWithEmailPassword();
        });

        // Event listener for Google login button
        cmdLoginGoogle.addActionListener((e) -> {
            loginWithGoogle();
        });
    }

    private void loginWithEmailPassword() {
		String email = txtEmail.getText().trim();
		String password = txtPassword.getText().trim();
    	String jwtToken;
		try {
			jwtToken = authenticationController.loginWithEmailPassword(email,password );
			if (jwtToken != null) {
	            TokenManager.writeEncryptedTokenToFile(TokenManager.encryptToken(jwtToken));
	            FormManager.reloadFrame();
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
            Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Login failed: " + e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loginWithGoogle() {
        try {
            HttpServerHandler.startHttpServer(this);
            Desktop.getDesktop().browse(new URI("https://savorgo-2003.web.app"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void handleGoogleCallback(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        String idToken = null;
        System.out.println("query:"+query);
        if (query != null && query.contains("token=")) {
            idToken = query.split("token=")[1].split("&")[0];
        }

        try {
            // Dừng server ngay sau khi nhận token
            HttpServerHandler.stopHttpServer();

            // Gửi ID Token lên backend để nhận JWT
            if (idToken != null) {
                String jwtToken = authenticationController.loginWithGoogle(idToken);

                if (jwtToken != null) {
                    TokenManager.writeEncryptedTokenToFile(TokenManager.encryptToken(jwtToken));
                    FormManager.reloadFrame();
                }
            }
        } catch (IOException e) {
            Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Login failed: " + e.getMessage());
        } catch (Exception e) {
            Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Error processing token: " + e.getMessage());
        }
    }

}
