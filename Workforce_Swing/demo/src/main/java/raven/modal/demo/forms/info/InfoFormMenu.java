package raven.modal.demo.forms.info;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.controllers.ControllerMenu;
import raven.modal.demo.forms.input.PopupFormBasic;
import raven.modal.demo.models.ModelMenu;

import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InfoFormMenu extends PopupFormBasic<ModelMenu> {
    private ControllerMenu controllerMenu;
    private ModelMenu modelMenu;

    public InfoFormMenu(String id) throws IOException {
        controllerMenu = new ControllerMenu();
        modelMenu = controllerMenu.getMenuById(id);
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
        JLabel lb = new JLabel("Menu Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0"); // Add title label to the panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /** 
     * Create fields to display the menu information.
     */
    @Override
    protected void createFields() {
        addField("Menu ID:", modelMenu.getId());
        addField("Menu Name:", modelMenu.getName());
        addField("Category:", modelMenu.getCategory());
        addField("Description:", modelMenu.getDescription());
        addField("Original Price:", String.valueOf(modelMenu.getOriginalPrice()));
        addField("Sale Price:", String.valueOf(modelMenu.getSalePrice()));
        addField("Status:", modelMenu.getStatus());
        addListField("Sizes:", modelMenu.getSizes());
        addListField("Options:", modelMenu.getOptions());
        addField("Created At:", modelMenu.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Updated At:", modelMenu.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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

    /** 
     * Add a list field to the panel with a label and a non-editable text area.
     * @param fieldName The name of the field.
     * @param list The list of values to display.
     */
    private void addListField(String fieldName, List<?> list) {
        contentPanel.add(new JLabel(fieldName), "gapy 5 0"); // Add the field name label
        JTextArea textArea = new JTextArea(); // Create a text area for the list
        textArea.setEditable(false); // Make the text area non-editable
        textArea.setLineWrap(true); // Enable line wrapping
        textArea.setWrapStyleWord(true); // Wrap by word
        if (list != null && !list.isEmpty()) {
            for (Object item : list) {
                textArea.append(item.toString() + "\n"); // Append each item in the list
            }
        } else {
            textArea.setText("N/A"); // Set N/A if the list is empty or null
        }
        contentPanel.add(new JScrollPane(textArea), "growx"); // Add the text area to the panel within a scroll pane
    }
}
