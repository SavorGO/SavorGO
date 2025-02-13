package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import raven.modal.component.AdaptSimpleModalBorder;
import com.formdev.flatlaf.FlatClientProperties;

public class EnterOtpInputForm extends PopupFormBasic implements InputPopupForm {
    private JTextField txtOtp = new JTextField();
    private JLabel lblOtpError = new JLabel();
    private boolean isOtpValid = false;

    public EnterOtpInputForm() {
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
        JLabel lb = new JLabel("Enter OTP");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0");
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0");
    }

    @Override
    protected void createFields() {
        JLabel lblOtp = new JLabel("OTP Code");
        lblOtp.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblOtp, "gapy 5 0");
        
        txtOtp.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter OTP Code");
        txtOtp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput();
            }
        });
        
        contentPanel.add(txtOtp);
        contentPanel.add(lblOtpError);
    }

    @Override
    public boolean validateInput() {
        isOtpValid = txtOtp.getText().trim().matches("\\d{6}"); // OTP phải là 6 chữ số
        lblOtpError.setText(isOtpValid ? "OTP is valid." : "OTP must be a 6-digit number.");
        lblOtpError.setForeground(isOtpValid ? Color.GREEN : Color.RED);

        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
                .getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isOtpValid);
        }
        return isOtpValid;
    }

    @Override
    public Object[] getData() {
        return new Object[] { txtOtp.getText().trim() };
    }
}
