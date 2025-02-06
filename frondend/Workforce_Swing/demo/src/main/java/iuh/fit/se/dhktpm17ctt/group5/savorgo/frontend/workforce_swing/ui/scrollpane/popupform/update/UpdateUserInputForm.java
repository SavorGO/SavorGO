package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update;

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
import java.io.IOException;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.UserController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserRoleEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserTierEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import raven.modal.component.AdaptSimpleModalBorder;
import com.formdev.flatlaf.FlatClientProperties;

public class UpdateUserInputForm extends PopupFormBasic implements InputPopupForm {
    private UserController userController = new UserController();
    private JTextField txtId = new JTextField(); // New field for ID
    private JTextField txtFirstName = new JTextField();
    private JTextField txtLastName = new JTextField();
    private JComboBox<String> cmbRole = new JComboBox<>(UserRoleEnum.getDisplayNames());
    private JSpinner spinnerPoints = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private JComboBox<String> cmbTier = new JComboBox<>(UserTierEnum.getDisplayNames());
    private JTextField txtAddress = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JPasswordField txtConfirmPassword = new JPasswordField();
    private JLabel lblImagePreview = new JLabel();
    private JTextField txtImagePath = new JTextField();
    private JButton btnClearImage = new JButton("Clear Image");
    private JLabel lblFirstNameError = new JLabel();
    private JLabel lblLastNameError = new JLabel();
    private JLabel lblPasswordError = new JLabel();
    private JLabel lblConfirmPasswordError = new JLabel();
    private JLabel lblAddressError = new JLabel();
    private boolean isInputsValid = false;
    private User user;

    public UpdateUserInputForm(User user) throws IOException {
        super();
        this.user = user;
        init();
    }

    @Override
    protected void init() throws IOException {
        createTitle(); // Create the title of the form
        createFields(); // Create the input fields
        setViewportView(contentPanel); // Set the content panel as the viewport
        setValue();
        validateInput();
    }

    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Update User Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0");
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0");
    }

    @Override
    protected void createFields() {
        // ID
        JLabel lblId = new JLabel("User  ID");
        lblId.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblId, "gapy 5 0");
        txtId.setEditable(false); // Make the ID field non-editable
        contentPanel.add(txtId, "wrap");

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
        contentPanel.add(lblFirstNameError);

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
        contentPanel.add(lblLastNameError);

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
        contentPanel.add(spinnerPoints);
        spinnerPoints.addChangeListener(e -> validateInput());

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
        contentPanel.add(lblPasswordError);

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
        contentPanel.add(lblConfirmPasswordError);

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
        contentPanel.add(lblAddressError);

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
                int maxWidth = Math.min(parentWidth - 60, 500);
                int maxHeight = (int) (maxWidth * 2.0 / 3);
                lblImagePreview.setMaximumSize(new Dimension(maxWidth, maxHeight));
                ImageIcon icon = (ImageIcon) lblImagePreview.getIcon();
                if (icon == null) return;
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

    private void setValue() throws IOException {
        txtId.setText(user.getId()); // Set the ID field
        txtFirstName.setText(user.getFirstName());
        txtLastName.setText(user.getLastName());
        cmbRole.setSelectedItem(user.getRole().getDisplayName());
        spinnerPoints.setValue(user.getPoints());
        txtAddress.setText(user.getAddress());
        txtImagePath.setText(user.getPublicId());
        // Optionally set the image preview if you have a method to get the image
		lblImagePreview.setIcon(userController.getImage(user,340, 340 / 3 * 2, 0));
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
            lblImagePreview.setIcon(new ImageIcon(selectedFilePath));
            validateInput();
        }
    }

    private void clearImage() {
        lblImagePreview.setIcon(null);
        txtImagePath.setText("");
        validateInput();
    }

    @Override
    public boolean validateInput() {
        isInputsValid = false;

        // Validate First Name
        boolean isFirstNameValid = txtFirstName.getText().matches("^[\\p{L} ]+$");
        lblFirstNameError.setText(isFirstNameValid ? "First Name is valid." : "First Name can only contain letters and spaces.");
        lblFirstNameError.setForeground(isFirstNameValid ? Color.GREEN : Color.RED);

        // Validate Last Name
        boolean isLastNameValid = txtLastName.getText().matches("^[\\p{L} ]+$");
        lblLastNameError.setText(isLastNameValid ? "Last Name is valid." : "Last Name can only contain letters and spaces.");
        lblLastNameError.setForeground(isLastNameValid ? Color.GREEN : Color.RED);

        // Validate Confirm Password
        boolean isConfirmPasswordValid = (txtConfirmPassword.getPassword().length > 0 ||  txtPassword.getPassword().length == 0)
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
        isInputsValid = isFirstNameValid && isLastNameValid && isConfirmPasswordValid
                && isAddressValid && isImageValid;

        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
                .getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isInputsValid);
        }
        return isInputsValid;
    }

    @Override
    public Object[] getData() {
        return new Object[] { user.getId(), txtFirstName.getText(), txtLastName.getText(),
                UserRoleEnum.fromDisplayName(cmbRole.getSelectedItem().toString()), spinnerPoints.getValue(),
                UserTierEnum.fromDisplayName(cmbTier.getSelectedItem().toString()), txtAddress.getText(),
                txtImagePath.getText(), String.valueOf(txtPassword.getPassword()) };
    }
}