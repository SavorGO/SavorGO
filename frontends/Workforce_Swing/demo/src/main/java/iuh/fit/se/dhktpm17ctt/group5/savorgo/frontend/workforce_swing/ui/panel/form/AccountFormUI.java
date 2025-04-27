package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form;

import com.formdev.flatlaf.FlatClientProperties;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.AuthenticationController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.UserController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.ChangePasswordInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreateUserInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.SystemForm;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@SystemForm(name = "Account Settings", description = "User account settings and options", tags = { "account",
		"settings" })
public class AccountFormUI extends Form {
	private AuthenticationController authenticationController = new AuthenticationController();
	private UserController userController = new UserController();
	private User user;

	public AccountFormUI() throws IOException {
		init();
	}

	protected void init() {
		try {
			user = authenticationController.verifyJwtToken();
		} catch (Exception e) {
			Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Lỗi khi giải token: " + e.getMessage());
		}

		if (user == null) {
			return;
		}

		// Sử dụng MigLayout thay vì BorderLayout
		setLayout(new MigLayout("fill, insets 10", "[grow]", "[grow][]"));

		// Panel chứa thông tin người dùng
		JPanel userInfoPanel = createUserInfoPanel();
		add(userInfoPanel, "grow, wrap");

		// Panel chứa các button
		JPanel buttonsPanel = createButtonsPanel();
		add(buttonsPanel, "growx, aligny bottom");
	}
	
	private Form thisForm = this;

	private JPanel createUserInfoPanel() {
	    JPanel panel = new JPanel(new MigLayout("fillx, wrap 1", "[grow, fill]", "[]"));

	    JPanel userCard = new JPanel(new MigLayout("align left top, gap 10, fillx", "[fill]10[grow,fill]", "[]"));

	    try {
	        MyImageIcon imageIcon = userController.getImage(user, 500, 500, 0);
	        if (imageIcon != null) {
	            JLabel imageIconlbl = new JLabel(imageIcon);
	            JPanel imagePanel = new JPanel(new MigLayout("fill"));
	            imagePanel.add(imageIconlbl, "grow"); // Loại bỏ w 300lp!
	            userCard.add(imagePanel);
	        }
	    } catch (IOException e) {
	        e.printStackTrace(); // Xử lý lỗi khi lấy ảnh
	    }

	    // Tạo các dòng thông tin người dùng và thêm vào bên phải
	    JPanel infoPanel = new JPanel(new MigLayout("fillx, wrap 2", "[][grow,fill]"));
	    infoPanel.add(new JLabel("User ID:"));
	    infoPanel.add(new JLabel(user.getId()));
	    infoPanel.add(new JLabel("Email:"));
	    infoPanel.add(new JLabel(user.getEmail()));
	    infoPanel.add(new JLabel("First Name:"));
	    infoPanel.add(new JLabel(user.getFirstName()));
	    infoPanel.add(new JLabel("Last Name:"));
	    infoPanel.add(new JLabel(user.getLastName()));
	    infoPanel.add(new JLabel("Role:"));
	    infoPanel.add(new JLabel(user.getRole().getDisplayName()));
	    infoPanel.add(new JLabel("Points:"));
	    infoPanel.add(new JLabel(String.valueOf(user.getPoints())));
	    infoPanel.add(new JLabel("Tier:"));
	    infoPanel.add(new JLabel(user.getTier().getDisplayName()));
	    infoPanel.add(new JLabel("Created At:"));
	    infoPanel.add(new JLabel(user.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	    infoPanel.add(new JLabel("Updated At:"));
	    infoPanel.add(new JLabel(user.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

	    userCard.add(infoPanel, "growx"); // Đảm bảo infoPanel cũng mở rộng

	    panel.add(userCard, "growx"); // Cho userCard mở rộng theo chiều ngang của panel chính

	    return panel;
	}
	private JPanel createUserInfoRow(String label, String value) {
	    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));  // Điều chỉnh hgap, vgap là 5
	    rowPanel.add(new JLabel(label));
	    JTextField textField = new JTextField(value, 20);
	    textField.setEditable(false);
	    rowPanel.add(textField);
	    return rowPanel;
	}

	private JPanel createButtonsPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		// Log Out button
		JButton logoutButton = new JButton("Log Out");
		logoutButton.putClientProperty(FlatClientProperties.STYLE, "font: bold");
		logoutButton.addActionListener(e -> logoutAction());
		panel.add(logoutButton);

		// Change Password button
		JButton changePasswordButton = new JButton("Change Password");
		changePasswordButton.putClientProperty(FlatClientProperties.STYLE, "font: bold");
		changePasswordButton.addActionListener(e -> changePasswordAction());
		panel.add(changePasswordButton);

		return panel;
	}

	private void logoutAction() {
		FormManager.doLogout();
	}

	private void changePasswordAction() {
		ChangePasswordInputForm updatePasswordInputForm = new ChangePasswordInputForm();
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(updatePasswordInputForm, "Update password",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleUpdatePassword(updatePasswordInputForm);
					}
				}), DefaultComponent.getInputForm());
	}
	
	private void handleUpdatePassword(ChangePasswordInputForm updatePasswordInputForm) {
		Object[] data = updatePasswordInputForm.getData();
		if (data == null) {
			return;
		}
		try {
			authenticationController.changePassword(data);
		} catch (Exception e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to update password: " + e.getMessage());
		}
	}
}