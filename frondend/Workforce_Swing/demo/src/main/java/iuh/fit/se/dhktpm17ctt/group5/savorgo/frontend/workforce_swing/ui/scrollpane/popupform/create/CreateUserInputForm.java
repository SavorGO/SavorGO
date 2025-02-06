package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserRoleEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserTierEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import raven.modal.component.AdaptSimpleModalBorder;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * InputFormCreateUser is a form for creating a user. It allows the user to
 * input various details about the user, including their name, email, role,
 * points, tier, address, and an image.
 */
public class CreateUserInputForm extends PopupFormBasic implements InputPopupForm {

	private JTextField txtEmail = new JTextField(); // Text field for user email
	private JTextField txtFirstName = new JTextField(); // Text field for first name
	private JTextField txtLastName = new JTextField(); // Text field for last name
	private JComboBox<String> cmbRole = new JComboBox<>(UserRoleEnum.getDisplayNames()); // Combo box for user role
	private JSpinner spinnerPoints = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)); // Spinner for
																										// points
	private JComboBox<String> cmbTier = new JComboBox<>(UserTierEnum.getDisplayNames()); // Combo box for user tier
	private JTextField txtAddress = new JTextField(); // Text field for address
	private JPasswordField txtPassword = new JPasswordField(); // Password field for user password
	private JPasswordField txtConfirmPassword = new JPasswordField(); // Password field for confirming password
	private JLabel lblImagePreview = new JLabel(); // Label to display the selected image
	private JTextField txtImagePath = new JTextField(); // TextField to display the selected image path
	private JButton btnClearImage = new JButton("Clear Image"); // Button to clear the selected image
	private JLabel lblEmailError = new JLabel(); // Error label for email
	private JLabel lblFirstNameError = new JLabel(); // Error label for first name
	private JLabel lblLastNameError = new JLabel(); // Error label for last name
	private JLabel lblPasswordError = new JLabel(); // Error label for password
	private JLabel lblConfirmPasswordError = new JLabel(); // Error label for confirm password
	private JLabel lblAddressError = new JLabel(); // Error label for address
	private boolean isInputsValid = false; // Flag to check if inputs are valid

	public CreateUserInputForm() {
		init(); // Initialize the form
	}

	@Override
	protected void init() {
		createTitle(); // Create the title of the form
		createFields(); // Create the input fields
		setViewportView(contentPanel); // Set the content panel as the viewport
		validateInput(); // Validate inputs initially
	}

	@Override
	protected void createTitle() {
		JLabel lb = new JLabel("User  Information"); // Title label
		lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
		contentPanel.add(lb, "gapy 5 0");
		contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Separator line
	}

	@Override
	protected void createFields() {
		// Email
		JLabel lblEmail = new JLabel("Email");
		lblEmail.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblEmail, "gapy 5 0");
		txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");
		txtEmail.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput();
			}
		});
		contentPanel.add(txtEmail);
		contentPanel.add(lblEmailError); // Add the error label to the content panel

		// First Name
		JLabel lblFirstName = new JLabel("First Name");
		lblFirstName.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblFirstName, "gapy 5 0");
		txtFirstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "First Name");
		txtFirstName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput();
			}
		});
		contentPanel.add(txtFirstName);
		contentPanel.add(lblFirstNameError); // Add the error label to the content panel

		// Last Name
		JLabel lblLastName = new JLabel("Last Name");
		lblLastName.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblLastName, "gapy 5 0");
		txtLastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Last Name");
		txtLastName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput();
			}
		});
		contentPanel.add(txtLastName);
		contentPanel.add(lblLastNameError); // Add the error label to the content panel

		// Role
		JLabel lblRole = new JLabel("Role");
		lblRole.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblRole, "gapy 5 0");
		cmbRole.addActionListener(e -> validateInput());
		contentPanel.add(cmbRole);

		// Points
		JLabel lblPoints = new JLabel("Points");
		lblPoints.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblPoints, "gapy 5 0");
		contentPanel.add(spinnerPoints); // Add the spinner instead of text field
		spinnerPoints.addChangeListener(e -> validateInput()); // Add listener for spinner

		// Password
		JLabel lblPassword = new JLabel("Password");
		lblPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblPassword, "gapy 5 0");
		txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput();
			}
		});
		contentPanel.add(txtPassword);
		contentPanel.add(lblPasswordError); // Add the error label to the content panel

		// Confirm Password
		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblConfirmPassword, "gapy 5 0");
		txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm Password");
		txtConfirmPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput();
			}
		});
		contentPanel.add(txtConfirmPassword);
		contentPanel.add(lblConfirmPasswordError); // Add the error label to the content panel

		// Tier
		JLabel lblTier = new JLabel("Tier");
		lblTier.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblTier, "gapy 5 0");
		cmbTier.addActionListener(e -> validateInput());
		contentPanel.add(cmbTier);

		// Address
		JLabel lblAddress = new JLabel("Address");
		lblAddress.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblAddress, "gapy 5 0");
		txtAddress.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Address");
		txtAddress.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput();
			}
		});
		contentPanel.add(txtAddress);
		contentPanel.add(lblAddressError); // Add the error label to the content panel

		// Image Section
		JLabel lblImage = new JLabel("Profile Image");
		lblImage.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblImage, "gapy 5 0");

		JButton btnChooseFile = new JButton("Choose Image File");
		btnChooseFile.addActionListener(e -> chooseImageFile());
		contentPanel.add(btnChooseFile, "wrap");

		lblImagePreview.setPreferredSize(new Dimension(200, 200));
		lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		contentPanel.add(lblImagePreview, "span, grow, wrap");

		lblImagePreview.getParent().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int parentWidth = lblImagePreview.getParent().getWidth();
                int maxWidth = Math.min(parentWidth - 60, 500); // Limit width
                int maxHeight = (int) (maxWidth * 2.0 / 3); // Calculate height based on 2:3 ratio
                lblImagePreview.setMaximumSize(new Dimension(maxWidth, maxHeight));
                // Scale image
                ImageIcon icon = (ImageIcon) lblImagePreview.getIcon();
                if (icon == null)
                    return;
                Image img = icon.getImage().getScaledInstance(maxWidth, maxHeight, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(img));
                lblImagePreview.revalidate();
                lblImagePreview.repaint();
            }
        });
		
		txtImagePath.setEditable(false);
		txtImagePath.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Image Path");
		contentPanel.add(txtImagePath, "wrap");

		btnClearImage.addActionListener(e -> clearImage());
		contentPanel.add(btnClearImage, "wrap");
	}

	private void chooseImageFile() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String selectedFilePath = selectedFile.getAbsolutePath();
			txtImagePath.setText(selectedFilePath);
			// Set the selected image in the JLabel
			ImageIcon icon = new ImageIcon(selectedFilePath);
			Image img = icon.getImage().getScaledInstance(icon.getIconWidth(), icon.getIconHeight(),
					Image.SCALE_SMOOTH);
			lblImagePreview.setIcon(new ImageIcon(img));
			lblImagePreview.setHorizontalAlignment(JLabel.CENTER);
			lblImagePreview.setVerticalAlignment(JLabel.CENTER);
			validateInput();
		}
	}

	private void clearImage() {
		lblImagePreview.setIcon(null); // Clear image from label
		validateInput();
	}

	/**
	 * Validates the input fields and updates the error labels accordingly.
	 * 
	 * @return true if all inputs are valid, false otherwise
	 */
	@Override
	public boolean validateInput() {
		isInputsValid = false;

		// Validate Email
		boolean isEmailValid = false;
		String emailText = txtEmail.getText();
		if (emailText.isEmpty() || !emailText.contains("@")) {
			lblEmailError.setText("Invalid email address.");
			lblEmailError.setForeground(Color.RED);
		} else {
			isEmailValid = true;
			lblEmailError.setText("Email is valid.");
			lblEmailError.setForeground(Color.GREEN);
		}

		// Validate First Name
		boolean isFirstNameValid = !txtFirstName.getText().isEmpty();
		lblFirstNameError.setText(isFirstNameValid ? "First Name is valid." : "First Name cannot be empty.");
		lblFirstNameError.setForeground(isFirstNameValid ? Color.GREEN : Color.RED);

		// Validate Last Name
		boolean isLastNameValid = !txtLastName.getText().isEmpty();
		lblLastNameError.setText(isLastNameValid ? "Last Name is valid." : "Last Name cannot be empty.");
		lblLastNameError.setForeground(isLastNameValid ? Color.GREEN : Color.RED);

		// Validate Password
		boolean isPasswordValid = txtPassword.getPassword().length > 0;
		lblPasswordError.setText(isPasswordValid ? "Password is valid." : "Password cannot be empty.");
		lblPasswordError.setForeground(isPasswordValid ? Color.GREEN : Color.RED);

		// Validate Confirm Password
		boolean isConfirmPasswordValid = txtConfirmPassword.getPassword().length > 0
				&& String.valueOf(txtPassword.getPassword()).equals(String.valueOf(txtConfirmPassword.getPassword()));
		lblConfirmPasswordError.setText(isConfirmPasswordValid ? "Passwords match." : "Passwords do not match.");
		lblConfirmPasswordError.setForeground(isConfirmPasswordValid ? Color.GREEN : Color.RED);

		// Validate Address
		boolean isAddressValid = !txtAddress.getText().isEmpty();
		lblAddressError.setText(isAddressValid ? "Address is valid." : "Address cannot be empty.");
		lblAddressError.setForeground(isAddressValid ? Color.GREEN : Color.RED);

		// Validate Image
		boolean isImageValid = lblImagePreview.getIcon() != null;
		if (!isImageValid) {
			txtImagePath.setText("Image cannot be null.");
			txtImagePath.setForeground(Color.RED);
		} else {
			txtImagePath.setForeground(Color.GREEN);
		}

		// Final validation
		isInputsValid = isEmailValid && isFirstNameValid && isLastNameValid && isPasswordValid && isConfirmPasswordValid
				&& isAddressValid && isImageValid;

		AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
				.getAncestorOfClass(AdaptSimpleModalBorder.class, this);
		if (modal != null) {
			modal.setOkButtonEnabled(isInputsValid);
		}
		return isInputsValid;
	}

	/**
	 * Returns an array of data representing the user details.
	 * 
	 * @return an array containing the user details
	 */
	@Override
	public Object[] getData() {
		return new Object[] { txtEmail.getText(), txtFirstName.getText(), txtLastName.getText(),
				UserRoleEnum.fromDisplayName(cmbRole.getSelectedItem().toString()), spinnerPoints.getValue(),
				UserTierEnum.fromDisplayName(cmbTier.getSelectedItem().toString()), txtAddress.getText(),
				txtImagePath.getText(), String.valueOf(txtPassword.getPassword()) };
	}
}