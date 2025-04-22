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
			// TODO Auto-generated catch block
			Toast.show(FormManager.getFrame(), Toast.Type.ERROR, "Lỗi khi giải token: " + e.getMessage());
		}

		if (user == null) {
			return;
		}

		// Thay đổi layout thành GridBagLayout
		setLayout(new BorderLayout(10, 10));

		// Panel chứa thông tin người dùng
		JPanel userInfoPanel = createUserInfoPanel();
		add(userInfoPanel, BorderLayout.CENTER);

		// Panel chứa các button
		JPanel buttonsPanel = createButtonsPanel();
		add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	private Form thisForm = this;

	private JPanel createUserInfoPanel() {
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	    JPanel userCard = new JPanel();
	    userCard.setLayout(new BorderLayout(10, 10));  // Sử dụng BorderLayout

	    try {
	        MyImageIcon imageIcon = userController.getImage(user, 1960, 1080, 0);
	        if (imageIcon != null) {
	            JLabel imageIconlbl = new JLabel(imageIcon);
	            imageIconlbl.setPreferredSize(new Dimension(600,350));
	            JPanel imagePanel = new JPanel(new BorderLayout());  // Tạo panel riêng cho ảnh
	            imagePanel.add(imageIconlbl, BorderLayout.CENTER);
	            imageIconlbl.getParent().addComponentListener(new ComponentAdapter() {
	                @Override
	                public void componentResized(ComponentEvent e) {
	                    int parentWidth = thisForm.getWidth();
	                    int width = parentWidth /2;
	                    int height = (int) (width * 2.0 / 3); // Tính chiều cao theo tỷ lệ 2:3
	                    imageIconlbl.setMaximumSize(new Dimension(width, height));
	                    imageIconlbl.setMinimumSize(new Dimension(width, height));
	                    // Thu nhỏ hình ảnh
	                    ImageIcon icon = (ImageIcon) imageIconlbl.getIcon();
	                    if (icon == null) return;
	                    Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
	                    imageIconlbl.setIcon(new ImageIcon(img));
	                    imageIconlbl.revalidate();
	                    imageIconlbl.repaint();
	                }
	            });
	            userCard.add(imagePanel, BorderLayout.WEST);  // Đặt ảnh bên trái
	        }
	    } catch (IOException e) {
	        e.printStackTrace(); // Xử lý lỗi khi lấy ảnh
	    }

	    // Tạo các dòng thông tin người dùng và thêm vào bên phải
	    JPanel infoPanel = new JPanel();
	    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
	    infoPanel.add(createUserInfoRow("User ID:", user.getId()));
	    infoPanel.add(createUserInfoRow("Email:", user.getEmail()));
	    infoPanel.add(createUserInfoRow("First Name:", user.getFirstName()));
	    infoPanel.add(createUserInfoRow("Last Name:", user.getLastName()));
	    infoPanel.add(createUserInfoRow("Role:", user.getRole().getDisplayName()));
	    infoPanel.add(createUserInfoRow("Points:", String.valueOf(user.getPoints())));
	    infoPanel.add(createUserInfoRow("Tier:", user.getTier().getDisplayName()));
	    infoPanel.add(createUserInfoRow("Created At:", user.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	    infoPanel.add(createUserInfoRow("Updated At:", user.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

	    userCard.add(infoPanel, BorderLayout.CENTER);  // Đặt thông tin người dùng bên phải

	    panel.add(userCard);

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
			// TODO Auto-generated catch block
			Toast.show(this, Toast.Type.ERROR, "Failed to update password: " + e.getMessage());
		}
	}
}
