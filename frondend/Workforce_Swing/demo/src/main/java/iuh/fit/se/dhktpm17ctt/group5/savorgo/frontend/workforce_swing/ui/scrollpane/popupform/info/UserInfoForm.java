package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info;

import com.formdev.flatlaf.FlatClientProperties;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.UserController ;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserInfoForm  extends PopupFormBasic {
    private UserController  userController ;
    private User user ;
    
    public UserInfoForm (String id) throws IOException {
        userController  = new UserController ();
        user  = userController .getUserById(id);
        init();
    }

    @Override
    protected void init() {
        createTitle(); // Create the title and add it to the content panel
        createFields(); // Create fields and add them to the content panel
        setViewportView(contentPanel); // Set the content panel to the scroll pane
    }

    /** 
     * Create the title label for the form.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("User  Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0"); // Add title label to the panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /** 
     * Create fields to display the user information.
     */
    @Override
    protected void createFields() {
    	if (userController.getThumbnailCell(user) != null) {
			contentPanel.add(DefaultComponent.createThumbnailPanel(userController.getThumbnailCell(user), true), "gapy 5 0");
		}
        addField("User ID:", user .getId()+"");
        addField("Email:", user .getEmail());
        addField("First Name:", user .getFirstName());
        addField("Last Name:", user .getLastName());
        addField("Role:", user .getRole().toString());
        addField("Points:", String.valueOf(user .getPoints()));
        addField("Tier:", user .getTier().toString());
        addField("Created At:", user .getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Updated At:", user .getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /** 
     * Add a field to the panel with a label and a non-editable text field.
     * @param fieldName The name of the field.
     * @param fieldValue The value of the field.
     */
    private void addField(String fieldName, String fieldValue) {
        contentPanel.add(new JLabel(fieldName), "gapy 5 0"); // Add the field name label
        JTextField textField = new JTextField(fieldValue); // Create a text field with the field value
        textField.setEditable(false); // Make the text field non-editable
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, fieldName); // Set placeholder text
        contentPanel.add(textField); // Add the text field to the panel
    }
}