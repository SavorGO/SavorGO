package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import raven.modal.component.AdaptSimpleModalBorder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class CreateTableInputForm extends PopupFormBasic<Table> implements InputPopupForm {

    private JTextField txtName = new JTextField(); // Declare txtName to access the value outside
    private JLabel lblNameError = new JLabel(); // Declare lblNameError to access the value outside
    private boolean isInputsValid = false; // Declare isInputsValid to access the value outside

    /**
     * Constructor for InputFormCreateTable. Initializes the form by calling the
     * init method.
     */
    public CreateTableInputForm() {
        init();
    }

    /**
     * Initializes the form components. This method sets up the title and fields for
     * the form.
     */
    @Override
    protected void init() {
        createTitle(); // Create the title for the form
        createFields(); // Create the input fields for the form
        setViewportView(contentPanel); // Set the content panel as the viewport for the scroll pane
        validateInput();
    }

    /**
     * Creates the title label for the form. This method adds a title label and a
     * separator to the content panel.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Table Information"); // Create a label for the title
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2"); // Set the font style for the title
        contentPanel.add(lb, "gapy 5 0"); // Add the title label to the content panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /**
     * Creates the input fields for the form. This method adds a label and a text
     * field for the table name to the content panel.
     */
    @Override
    protected void createFields() {
        JLabel lblName = new JLabel("Table Name"); // Create a label for the table name
        lblName.putClientProperty(FlatClientProperties.STYLE, "font:bold"); // Set the font style for the label
        contentPanel.add(lblName, "gapy 5 0"); // Add a label for the table name
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Table Name"); // Set placeholder text for the text field
        txtName.setColumns(255); // Set the number of columns for the text field
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput(); // Validate input on each key release
            }
        });
        contentPanel.add(txtName); // Add the text field to the content panel
        contentPanel.add(lblNameError); // Add the error label to the content panel
    }

    /**
     * Validates the input in the txtName field. This method checks the length of the
     * input and updates the error label accordingly.
     * 
     * @return true if the input is valid, false otherwise
     */
    @Override
    public boolean validateInput() {
        isInputsValid = false; // Assume the input is invalid
        String nameText = txtName.getText();
        // Set color for each
        if (nameText.length() > 255) {
            lblNameError.setText("Name cannot exceed 255 characters."); // Set error message
            lblNameError.setForeground(Color.RED); // Set the color of the error message
        } else if (nameText.isEmpty()) {
            lblNameError.setText("Name cannot be empty."); // Optional: handle empty input
            lblNameError.setForeground(Color.RED); // Set the color of the error message
        } else {
            lblNameError.setText("Name is OK."); // Show success message
            lblNameError.setForeground(Color.GREEN); // Set the color of the success message
            isInputsValid = true; // Set the input as valid
        }
        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities.getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isInputsValid);
        }
        return isInputsValid;
    }

    /**
     * Retrieves the data entered in the form.
     * 
     * @return an array containing the table name
     */
    @Override
    public Object[] getData() {
        return new Object[] { txtName.getText() };
    }
}