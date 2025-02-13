package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import raven.modal.component.AdaptSimpleModalBorder;
import com.formdev.flatlaf.FlatClientProperties;

public class ChangePasswordInputForm extends PopupFormBasic implements InputPopupForm {
    private JPasswordField txtOldPassword = new JPasswordField();
    private JPasswordField txtNewPassword = new JPasswordField();
    private JPasswordField txtConfirmNewPassword = new JPasswordField();
    private JLabel lblOldPasswordError = new JLabel();
    private JLabel lblNewPasswordError = new JLabel();
    private JLabel lblConfirmNewPasswordError = new JLabel();
    private boolean isInputsValid = false;

    public ChangePasswordInputForm() {
        super();
        init();
    }

    @Override
    protected void init() {
        createTitle(); // Create the title of the form
        createFields(); // Create the input fields
        setViewportView(contentPanel); // Set the content panel as the viewport
        validateInput();
    }

    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Update Password");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0");
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0");
    }

    @Override
    protected void createFields() {
        // Old Password
        JLabel lblOldPassword = new JLabel("Old Password");
        lblOldPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblOldPassword, "gapy 5 0");
        txtOldPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Old Password");
        txtOldPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput();
            }
        });
        contentPanel.add(txtOldPassword);
        contentPanel.add(lblOldPasswordError);

        // New Password
        JLabel lblNewPassword = new JLabel("New Password");
        lblNewPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblNewPassword, "gapy 5 0");
        txtNewPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter New Password");
        txtNewPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput();
            }
        });
        contentPanel.add(txtNewPassword);
        contentPanel.add(lblNewPasswordError);

        // Confirm New Password
        JLabel lblConfirmNewPassword = new JLabel("Confirm New Password");
        lblConfirmNewPassword.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblConfirmNewPassword, "gapy 5 0");
        txtConfirmNewPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Re-enter New Password");
        txtConfirmNewPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput();
            }
        });
        contentPanel.add(txtConfirmNewPassword);
        contentPanel.add(lblConfirmNewPasswordError);
    }

    @Override
    public boolean validateInput() {
        isInputsValid = false;

        // Validate Old Password (non-empty)
        boolean isOldPasswordValid = txtOldPassword.getPassword().length > 0;
        lblOldPasswordError.setText(isOldPasswordValid ? "Old Password is valid." : "Old Password cannot be empty.");
        lblOldPasswordError.setForeground(isOldPasswordValid ? Color.GREEN : Color.RED);

        // Validate New Password (non-empty, length >= 8)
        boolean isNewPasswordValid = false;
        if(txtNewPassword.getPassword().length < 0) {
            lblNewPasswordError.setText("New Password cannot be emty");
            lblNewPasswordError.setForeground(Color.RED);
        } else {
        	if(txtNewPassword.getPassword().equals(txtOldPassword.getPassword())) {
            	lblNewPasswordError.setText("New Password cannot be same as old password.");
            	lblNewPasswordError.setForeground(Color.RED);
        	} else {
        		isNewPasswordValid = true;
        		lblNewPasswordError.setText("New Password is valid.");
            	lblNewPasswordError.setForeground(Color.green);
        	}
        }

        // Validate Confirm New Password (must match New Password)
        boolean isConfirmNewPasswordValid = String.valueOf(txtNewPassword.getPassword()).equals(String.valueOf(txtConfirmNewPassword.getPassword()));
        lblConfirmNewPasswordError.setText(isConfirmNewPasswordValid ? "Passwords match." : "Passwords do not match.");
        lblConfirmNewPasswordError.setForeground(isConfirmNewPasswordValid ? Color.GREEN : Color.RED);

        // Final validation
        isInputsValid = isOldPasswordValid && isNewPasswordValid && isConfirmNewPasswordValid;

        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
                .getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isInputsValid);
        }
        return isInputsValid;
    }

    @Override
    public Object[] getData() {
        return new Object[] {
                String.valueOf(txtOldPassword.getPassword()),
                String.valueOf(txtNewPassword.getPassword()),
        };
    }
}
