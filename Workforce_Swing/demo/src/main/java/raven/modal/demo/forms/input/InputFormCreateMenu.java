package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.modal.demo.models.ModelMenu; // Updated to ModelMenu

import java.util.List;

import javax.swing.*;

public class InputFormCreateMenu extends PopupFormBasic<ModelMenu> {
    
    private JTextField txtName = new JTextField(); // Field for menu name
    private JTextField txtCategory = new JTextField(); // Field for category
    private JTextField txtOriginalPrice = new JTextField(); // Field for original price
    private JTextField txtSalePrice = new JTextField(); // Field for sale price
    private JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Available", "Out of Stock", "Discontinued"}); // Dropdown for status
    private JTextField txtSizes = new JTextField(); // Field for sizes
    private JTextField txtOptions = new JTextField(); // Field for options

    /** 
     * Constructor for InputFormCreateMenu.
     * Initializes the form by calling the init method.
     */
    public InputFormCreateMenu() {
        init();
    }

    /** 
     * Initialize the form components.
     * This method sets up the title and fields for the form.
     */
    @Override
    protected void init() {
        createTitle(); // Create the title for the form
        createFields(); // Create the input fields for the form
        setViewportView(contentPanel); // Set the content panel as the viewport for the scroll pane
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
     * Create the input fields for the form.
     * This method adds labels and text fields for the menu properties to the content panel.
     */
    @Override
    protected void createFields() {
        contentPanel.add(new JLabel("Menu Name"), "gapy 5 0"); // Add a label for the menu name
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Menu Name"); // Set placeholder text for the text field
        contentPanel.add(txtName); // Add the text field to the content panel

        contentPanel.add(new JLabel("Category"), "gapy 5 0"); // Add a label for the category
        txtCategory.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Category"); // Set placeholder text for the text field
        contentPanel.add(txtCategory); // Add the text field to the content panel

        contentPanel.add(new JLabel("Original Price"), "gapy 5 0"); // Add a label for the original price
        txtOriginalPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Original Price"); // Set placeholder text for the text field
        contentPanel.add(txtOriginalPrice); // Add the text field to the content panel

        contentPanel.add(new JLabel("Sale Price"), "gapy 5 0"); // Add a label for the sale price
        txtSalePrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sale Price"); // Set placeholder text for the text field
        contentPanel.add(txtSalePrice); // Add the text field to the content panel

        contentPanel.add(new JLabel("Status"), "gapy 5 0"); // Add a label for the status
        contentPanel.add(cmbStatus); // Add the dropdown for status

        contentPanel.add(new JLabel("Sizes"), "gapy 5 0"); // Add a label for sizes
        txtSizes.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sizes"); // Set placeholder text for the text field
        contentPanel.add(txtSizes); // Add the text field to the content panel

        contentPanel.add(new JLabel("Options"), "gapy 5 0"); // Add a label for options
        txtOptions.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Options"); // Set placeholder text for the text field
        contentPanel.add(txtOptions); // Add the text field to the content panel
    }

    /** 
     * Getter method to retrieve the menu information entered by the user.
     * @return A ModelMenu object containing the entered information.
     */
    public ModelMenu getMenu() {
        ModelMenu menu = new ModelMenu();
        menu.setName(txtName.getText());
        menu.setCategory(txtCategory.getText());
        menu.setOriginalPrice(Double.parseDouble(txtOriginalPrice.getText()));
        menu.setSalePrice(Double.parseDouble(txtSalePrice.getText()));
        menu.setStatus((String) cmbStatus.getSelectedItem());
        //menu.setSizes(txtSizes.getText());
        //menu.setOptions(txtOptions.getText());
        return menu; // Return the populated ModelMenu object
    }
 // Individual getters for each field

    public String getMenuName() {
        return txtName.getText().trim();
    }

    public String getMenuCategory() {
        return txtCategory.getText().trim();
    }

    public double getOriginalPrice() {
        try {
            return Double.parseDouble(txtOriginalPrice.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0; // Default to 0 if invalid input
        }
    }

    public double getSalePrice() {
        try {
            return Double.parseDouble(txtSalePrice.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0; // Default to 0 if invalid input
        }
    }

    public String getMenuStatus() {
        return (String) cmbStatus.getSelectedItem();
    }

    public String getMenuSizes() {
        return txtSizes.getText().trim();
    }

    public String getMenuOptions() {
        return txtOptions.getText().trim();
    }

    // Parsing methods for sizes and options (example implementation)
    private List<String> parseSizes(String sizesInput) {
        // Parse sizes string into list of Size objects
        // Example input: "Small:0, Medium:5"
        // Return appropriate list
        return null; // Implement as needed
    }

    private List<String> parseOptions(String optionsInput) {
        // Parse options string into list of Option objects
        // Example input: "Cheese:2, Bacon:3"
        // Return appropriate list
        return null; // Implement as needed
    }
}