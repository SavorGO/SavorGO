package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.TableController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import javax.swing.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class InfoFormTable extends PopupFormBasic<Table> {
    private TableController controllerTable;
    private Table modelTable;

    public InfoFormTable(long id) throws IOException {
        controllerTable = new TableController();
        modelTable = controllerTable.getTableById(id);
        init();
    }

    @Override
    protected void init() {
        // Set layout for the main panel
    	createTitle(); // Create the title and add it to the content panel
        createFields(); // Create fields and add them to the content panel
        setViewportView(contentPanel); // Set the content panel to the scroll pane
    }


    /** 
     * Create the title label for the form.
     * @param panel The panel to which the title will be added.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Table Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0"); // Add title label to the panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /** 
     * Create fields to display the table information.
     * @param panel The panel to which the fields will be added.
     * @param modelTable The model containing the table information.
     */
    @Override
    protected void createFields() {
        addField("Table ID:", String.valueOf(modelTable.getId()));
        addField("Table Name:", modelTable.getName());
        addField("Status:", modelTable.getStatus().getDisplayName());
        addField("Is Reserved:", modelTable.isReserved() ? "Yes" : "No");
        addField("Created At:", modelTable.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        addField("Updated At:", modelTable.getModifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /** 
     * Add a field to the panel with a label and a non-editable text field.
     * @param panel The panel to which the field will be added.
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