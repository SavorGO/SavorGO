package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.controllers.ControllerMenu;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.utils.BusinessException;
import raven.modal.demo.utils.DefaultComponent;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class InputFormUpdateMenu extends PopupFormBasic<ModelMenu> {

    private JTextField txtName; // Input field for menu name
    private JTextField txtCategory; // Input field for menu category
    private JFormattedTextField txtOriginalPrice; // Input field for original price
    private JFormattedTextField txtSalePrice; // Input field for sale price
    private JComboBox<String> cmbStatus; // Dropdown for menu status
    private JTextArea txtSizes; // Text area for sizes
    private JTextArea txtOptions; // Text area for options

    private ControllerMenu controllerMenu; // Controller to manage menu data
    private ModelMenu modelMenu; // Model representing the menu

    /**
     * Constructor for InputFormUpdateMenu.
     * Initializes the form and retrieves the menu data by ID.
     * @param id The ID of the menu to be updated.
     * @throws IOException If there is an error retrieving the menu data.
     */
    public InputFormUpdateMenu(String id) throws IOException {
        super(); // Call the constructor of the parent class
        controllerMenu = new ControllerMenu(); // Initialize the controller
        modelMenu = controllerMenu.getMenuById(id); // Retrieve the menu data
        init(); // Initialize the form components
    }

    /**
     * Initialize the form components.
     * This method sets up the layout and adds fields for menu information.
     */
    @Override
    protected void init() {
        createTitle(); // Create the title for the form
        createFields(); // Create input fields for menu information
        setViewportView(contentPanel); // Set the content panel as the viewport
    }

    /**
     * Create the title label for the form.
     * This method adds a title label and a separator to the content panel.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Menu Information"); // Create a label for the title
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2"); // Set the font style for the title
        contentPanel.add(lb, "gapy 5 0"); // Add the title label to the content panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /**
     * Create input fields for the menu information.
     * This method adds fields for menu name, category, prices, status, sizes, and options.
     */
    @Override
    protected void createFields() {
        // Input field for menu name
        txtName = new JTextField(modelMenu.getName());
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Menu Name");
        contentPanel.add(new JLabel("Menu Name"), "gapy 5 0"); // Add label for menu name
        contentPanel.add(txtName); // Add the text field for menu name

        // Input field for menu category
        txtCategory = new JTextField(modelMenu.getCategory());
        txtCategory.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Menu Category");
        contentPanel.add(new JLabel("Category"), "gapy 5 0"); // Add label for category
        contentPanel.add(txtCategory); // Add the text field for category

        // Input fields for original and sale prices
        txtOriginalPrice = new JFormattedTextField(modelMenu.getOriginalPrice());
        txtOriginalPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Original Price");
        txtSalePrice = new JFormattedTextField(modelMenu.getSalePrice());
        txtSalePrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sale Price");

        contentPanel.add(new JLabel("Original Price"), "gapy 5 0"); // Add label for original price
        contentPanel.add(txtOriginalPrice); // Add the text field for original price

        contentPanel.add(new JLabel("Sale Price"), "gapy 5 0"); // Add label for sale price
        contentPanel.add(txtSalePrice); // Add the text field for sale price

        // Dropdown for menu status
        String[] statuses = {"Available", "Unavailable"}; // Define possible statuses
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setSelectedItem(modelMenu.getStatus()); // Set the current status
        cmbStatus.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Select Status");
        contentPanel.add(new JLabel("Status"), "gapy 5 0"); // Add label for status
        contentPanel.add(cmbStatus); // Add the dropdown for status

        // Text area for sizes
        txtSizes = new JTextArea();
        txtSizes.setText(formatList(modelMenu.getSizes())); // Format and set sizes
        txtSizes.setRows(5);
        txtSizes.setLineWrap(true);
        txtSizes.setWrapStyleWord(true);
        JScrollPane sizeScrollPane = new JScrollPane(txtSizes);
        contentPanel.add(new JLabel("Sizes"), "gapy 5 0"); // Add label for sizes
        contentPanel.add(sizeScrollPane, "grow"); // Add the text area for sizes

        // Text area for options
        txtOptions = new JTextArea();
        txtOptions.setText(formatList(modelMenu.getOptions())); // Format and set options
        txtOptions.setRows(5);
        txtOptions.setLineWrap(true);
        txtOptions.setWrapStyleWord(true);
        JScrollPane optionScrollPane = new JScrollPane(txtOptions);
        contentPanel.add(new JLabel("Options"), "gapy 5 0"); // Add label for options
        contentPanel.add(optionScrollPane, "grow"); // Add the text area for options
    }

    /**
     * Format a list of sizes or options into a string.
     * @param list The list to be formatted.
     * @return A formatted string representing the list.
     */
    private <T> String formatList(List<T> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Getter method to retrieve the name entered by the user.
     * @return The text entered in the menu name field.
     */
    public String getMenuName() {
        return txtName.getText();
    }

    /**
     * Getter method to retrieve the category entered by the user.
     * @return The text entered in the category field.
     */
    public String getCategory() {
        return txtCategory.getText();
    }

    /**
     * Getter method to retrieve the original price entered by the user.
     * @return The value entered in the original price field.
     */
    public double getOriginalPrice() {
        return Double.parseDouble(txtOriginalPrice.getText());
    }

    /**
     * Getter method to retrieve the sale price entered by the user.
     * @return The value entered in the sale price field.
     */
    public double getSalePrice() {
        return Double.parseDouble(txtSalePrice.getText());
    }

    /**
     * Getter method to retrieve the selected menu status.
     * @return The selected status.
     */
    public String getStatus() {
        return (String) cmbStatus.getSelectedItem();
    }

    /**
     * Getter method to retrieve the sizes entered by the user.
     * @return A list of sizes.
     */
    public String getSizes() {
        return txtSizes.getText();
    }

    /**
     * Getter method to retrieve the options entered by the user.
     * @return A list of options.
     */
    public String getOptions() {
        return txtOptions.getText();
    }
}
