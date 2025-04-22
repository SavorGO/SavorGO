package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.auth;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.Demo;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.AuthenticationController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.menu.MyDrawerBuilder;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.Form;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.EnterOtpInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreateUserInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpServerHandler;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.TokenManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import java.awt.Color;
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
	private JButton btnLogin;
	private JButton btnLoginWithGoogle;
	private JButton btnForgotPassword; // Thêm nút "Quên mật khẩu"
	private AuthenticationController authenticationController = new AuthenticationController();

	public Login() {
		init();
	}

	private void init() {
		setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
		txtEmail = new JTextField();
		txtPassword = new JPasswordField();
		chRememberMe = new JCheckBox("Remember me");
		btnLogin = new JButton("Login");
		btnLoginWithGoogle = new JButton("Login with Google");
		btnForgotPassword = new JButton("Forgot Password? Click here!");
		btnForgotPassword.setOpaque(false);
		btnForgotPassword.setContentAreaFilled(false);
		btnForgotPassword.setBorderPainted(false);
		JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 35 45", "fill,250:280"));
		panel.putClientProperty(FlatClientProperties.STYLE, "" + "arc:20;"
				+ "[light]background:shade($Panel.background,5%);" + "[dark]background:tint($Panel.background,5%);");

		txtPassword.putClientProperty(FlatClientProperties.STYLE, "" + "showRevealButton:true");
		btnLogin.putClientProperty(FlatClientProperties.STYLE,
				"" + "[light]background:shade($Panel.background,10%);" + "[dark]background:tint($Panel.background,10%);"
						+ "borderWidth:0;" + "focusWidth:0;" + "innerFocusWidth:0");

		txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
		txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

		JLabel lbTitle = new JLabel("Welcome back!");
		JLabel description = new JLabel("Please sign in to access your account");
		lbTitle.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +10");
		description.putClientProperty(FlatClientProperties.STYLE, "" + "foreground:$Label.disabledForeground;");

		panel.add(lbTitle);
		panel.add(description);
		panel.add(new JLabel("Email"), "gapy 8");
		panel.add(txtEmail);
		panel.add(new JLabel("Password"), "gapy 8");
		panel.add(txtPassword);
		panel.add(chRememberMe, "split 2, grow 0"); 
		panel.add(btnForgotPassword, "align right, gapleft push");		panel.add(btnLogin, "gapy 10");
		panel.add(btnLoginWithGoogle, "gapy 10");
		add(panel);

		// Event listener for standard login button
		btnLogin.addActionListener((e) -> {
			loginWithEmailPassword();
		});

		// Event listener for Google login button
		btnLoginWithGoogle.addActionListener((e) -> {
			loginWithGoogle();
		});

		btnForgotPassword.addActionListener((e) -> forgotPassword());

	}

	private void loginWithEmailPassword() {
		String email = txtEmail.getText().trim();
		String password = txtPassword.getText().trim();
		String jwtToken;
		try {
			jwtToken = authenticationController.loginWithEmailPassword(email, password);
			if (jwtToken != null) {
				TokenManager.writeEncryptedTokenToFile(TokenManager.encryptToken(jwtToken));
				FormManager.reloadFrame();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Login failed: " + e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Login failed: " + e.getMessage());
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
		System.out.println("query:" + query);
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

	private void forgotPassword() {
		String email = txtEmail.getText().trim();
		if (email.isEmpty()) {
			Toast.show(FormManager.getFrame(), Toast.Type.WARNING, "Please enter your email firstly to reset password.");
			return;
		}

		try {
			boolean success = true;// authenticationController.forgotPassword(email);
			if (success) {
				Toast.show(FormManager.getFrame(), Toast.Type.SUCCESS,
						"A password reset link has been sent to your email.");
				EnterOtpInputForm enterOtpInputForm = new EnterOtpInputForm();
				ModalDialog.showModal(this, new AdaptSimpleModalBorder(enterOtpInputForm, "Enter OTP",
						AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								handleEnterOTP(enterOtpInputForm);
							}
						}), DefaultComponent.getInputForm());
			} else {
				Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Failed to send password reset email.");
			}
		} catch (Exception ex) {
			Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Error: " + ex.getMessage());
		}
	}
	private void handleEnterOTP(EnterOtpInputForm enterOtpInputForm) {
		Object[] data = enterOtpInputForm.getData();
		String otp = (String) data[0];
		try {
			boolean success = true;//authenticationController.resetPassword(otp);
			if (success) {
				Toast.show(FormManager.getFrame(), Toast.Type.SUCCESS, "Password reset successfully.");
			} else {
				Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Failed to reset password.");
			}
		} catch (Exception ex) {
			Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Error: " + ex.getMessage());
		}
	}		
}
