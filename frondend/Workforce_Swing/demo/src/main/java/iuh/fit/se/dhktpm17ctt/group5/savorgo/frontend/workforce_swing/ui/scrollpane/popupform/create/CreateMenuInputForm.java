package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuCategoryEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.MenuOption;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.MenuSize;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.CustomFormattedTextField;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;
import raven.modal.component.AdaptSimpleModalBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * InputFormCreateMenu is a form for creating a menu item. It allows the user to
 * input various details about the menu item, including its name, category,
 * prices, sizes, options, and an image.
 */
public class CreateMenuInputForm extends PopupFormBasic implements InputPopupForm {

    private JTextField txtName = new JTextField(); // Text field for menu name
    private CustomFormattedTextField txtOriginalPrice = new CustomFormattedTextField(); // Formatted text field for original price
    private CustomFormattedTextField txtSalePrice = new CustomFormattedTextField(); // Formatted text field for sale price
    private JComboBox<String> cmbCategory = new JComboBox<>(MenuCategoryEnum.getDisplayNames()); // Combo box for menu category
    private JLabel lblImagePreview = new JLabel(); // Label to display the selected image
    private JTextField txtImagePath = new JTextField(); // TextField to display the selected image path
    private JButton btnClearImage = new JButton("Clear Image"); // Button to clear the selected image
    private JTable tableSizes; // Table for sizes
    private DefaultTableModel sizeTableModel; // Model for sizes table
    private boolean isInputsValid = false; // Declare isInputsValid to access the value outside
    private JTextArea txtDescription = new JTextArea(); // Text area for description
    private JTable tableOptions; // Table for options
    private DefaultTableModel optionTableModel; // Model for options table
    private JLabel lblNameError = new JLabel();
    private JLabel lblOriginalPriceError = new JLabel();
    private JLabel lblSalePriceError = new JLabel(); // Declare lblNameError to access the value outside;
    private JLabel lblSizesError = new JLabel(); // Declare lblNameError to access the value outside;
    private JLabel lblOptionsError = new JLabel(); // Declare lblNameError to access the value outside;
    private JLabel lblDescriptionError = new JLabel(); // Declare lblNameError to access the value outside;

    public CreateMenuInputForm() {
        init(); // Initialize the form
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
        JLabel lb = new JLabel("Menu Information"); // Title label
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0");
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Separator line
    }

    @Override
    protected void createFields() {
        // Menu Name
        JLabel lblName = new JLabel("Menu Name");
        lblName.putClientProperty(FlatClientProperties.STYLE, "font:bold"); // Bold font
        contentPanel.add(lblName, "gapy 5 0");
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Menu Name");
        txtName.setColumns(255);
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput();
            }
        });
        contentPanel.add(txtName);
        contentPanel.add(lblNameError); // Add the error label to the content panel

        // Category
        JLabel lblCategory = new JLabel("Category");
        lblCategory.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblCategory, "gapy 5 0");
        contentPanel.add(cmbCategory);
        
        cmbCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateInput();
            }
        });

        // Original Price
        JLabel lblOriginalPrice = new JLabel("Original Price");
        lblOriginalPrice.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblOriginalPrice, "gapy 5 0");
        txtOriginalPrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Original Price");
        txtOriginalPrice.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                validateInput();
            }
        });
        contentPanel.add(txtOriginalPrice);
        contentPanel.add(lblOriginalPriceError); // Add the error label to the content panel

        // Sale Price
        JLabel lblSalePrice = new JLabel("Sale Price");
        lblSalePrice.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblSalePrice, "gapy 5 0");
        txtSalePrice.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Sale Price");
        txtSalePrice.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                validateInput();
            }
        });
        contentPanel.add(txtSalePrice);
        contentPanel.add(lblSalePriceError); // Add the error label to the content panel

        // Sizes Table
        JLabel lblSizes = new JLabel("Sizes");
        lblSizes.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblSizes, "gapy 5 0");
        createSizeTable();

        // Options Table
        JLabel lblOptions = new JLabel("Options");
        lblOptions.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblOptions, "gapy 5 0");
        createOptionTable();

        // Image Section
        JLabel lblImage = new JLabel("Image");
        lblImage.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblImage, "gapy 5 0");

        JButton btnChooseFile = new JButton("Choose Image File");
        btnChooseFile.addActionListener(e -> chooseImageFile());
        contentPanel.add(btnChooseFile, "wrap");

        lblImagePreview.setPreferredSize(new Dimension(200, 200));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.add(lblImagePreview, "span, grow, wrap, w 100%"); // Set max width to your desired value

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

        // Description
        JLabel lblDescription = new JLabel("Description");
        lblDescription.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblDescription, "gapy 5 0");

        // Create JTextArea with a preferred size
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter description here...");
        txtDescription.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateInput();
            }
        });
        // Add JTextArea to contentPanel with max width
        contentPanel.add(txtDescription, "span, grow, wrap, w 100%"); // Set max width to your desired value
        contentPanel.add(lblDescriptionError); // Add the error label to the content panel
    }

    private void createSizeTable() {
        sizeTableModel = new DefaultTableModel(new Object[] { "Size", "Price" }, 0);
        tableSizes = new JTable(sizeTableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 1) { // Column "Price"
                    CustomFormattedTextField txtSizeChange = new CustomFormattedTextField();
                    return new DefaultCellEditor(txtSizeChange);
                }
                return super.getCellEditor(row, column);
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableSizes);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        contentPanel.add(scrollPane, "wrap");

        // Panel containing Add and Delete buttons
        JPanel buttonPanel = new JPanel(new MigLayout("wrap 2", "[grow]10[grow, fill]")); // 2 columns
        JButton btnAddSize = new JButton("Add Size");
        btnAddSize.addActionListener(e -> sizeTableModel.addRow(new Object[] { "New size", 0 }));
        buttonPanel.add(btnAddSize, "grow");

        JButton btnDeleteSize = new JButton("Delete Size");
        btnDeleteSize.setEnabled(false); // Start in disabled state
        btnDeleteSize.addActionListener(e -> {
            int selectedRow = tableSizes.getSelectedRow();
            if (selectedRow != -1) {
                sizeTableModel.removeRow(selectedRow);
            }
        });
        buttonPanel.add(btnDeleteSize, "grow"); // Fixed width for Delete button

        contentPanel.add(buttonPanel, "grow, wrap");

        // Add listener to enable Delete button when a row is selected
        tableSizes.getSelectionModel().addListSelectionListener(e -> {
            btnDeleteSize.setEnabled(tableSizes.getSelectedRow() != -1);
        });

        tableSizes.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                validateInput();
            }
        });
        contentPanel.add(lblSizesError); // Add the error label to the content panel
    }

    private void createOptionTable() {
        optionTableModel = new DefaultTableModel(new Object[] { "Option", "Price" }, 0);
        tableOptions = new JTable(optionTableModel) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 1) { // Column "Price"
                    CustomFormattedTextField txtOptionChange = new CustomFormattedTextField();
                    return new DefaultCellEditor(txtOptionChange);
                }
                return super.getCellEditor(row, column);
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableOptions);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        contentPanel.add(scrollPane, "grow, wrap");

        // Panel containing Add and Delete buttons
        JPanel buttonPanel = new JPanel(new MigLayout("wrap 2", "[grow]10[grow, fill]")); // 2 columns
        JButton btnAddOption = new JButton("Add Option");
        btnAddOption.addActionListener(e -> optionTableModel.addRow(new Object[] { "New option", 0 }));
        buttonPanel.add(btnAddOption, "grow");

        JButton btnDeleteOption = new JButton("Delete Option");
        btnDeleteOption.setEnabled(false); // Start in disabled state
        btnDeleteOption.addActionListener(e -> {
            int selectedRow = tableOptions.getSelectedRow();
            if (selectedRow != -1) {
                optionTableModel.removeRow(selectedRow);
            }
        });
        buttonPanel.add(btnDeleteOption, "grow"); // Fixed width for Delete button

        contentPanel.add(buttonPanel, "wrap");
        contentPanel.add(lblOptionsError); // Add the error label to the content panel

        // Add listener to enable Delete button when a row is selected
        tableOptions.getSelectionModel().addListSelectionListener(e -> {
            btnDeleteOption.setEnabled(tableOptions.getSelectedRow() != -1);
        });
        tableOptions.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                validateInput();
            }
        });
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
                    Image.SCALE_SMOOTH); // Resize image to fit label
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
     * Returns a list of sizes from the size table as MenuSize objects.
     * Each size is represented as a MenuSize with size name and price.
     * 
     * @return a list of MenuSize objects
     */
    public List<MenuSize> getSizesFromTable() {
        int rowCount = sizeTableModel.getRowCount();
        List<MenuSize> sizes = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            String size = sizeTableModel.getValueAt(i, 0).toString();
            String price = sizeTableModel.getValueAt(i, 1).toString();
            sizes.add(new MenuSize(size, Double.parseDouble(price)));
        }
        return sizes;
    }

    /**
     * Returns a list of options from the option table as MenuOption objects.
     * Each option is represented as a MenuOption with option name and price.
     * 
     * @return a list of MenuOption objects
     */
    public List<MenuOption> getOptionsFromTable() {
        int rowCount = optionTableModel.getRowCount();
        List<MenuOption> options = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            String option = optionTableModel.getValueAt(i, 0).toString();
            String price = optionTableModel.getValueAt(i, 1).toString();
            options.add(new MenuOption(option, Double.parseDouble(price)));
        }
        return options;
    }

    /**
     * Validates the input fields and updates the error labels accordingly.
     * 
     * @return true if all inputs are valid, false otherwise
     */
    @Override
    public boolean validateInput() {
        isInputsValid = false;
        // Validate the name field
        boolean isNameValid = false;
        String nameText = txtName.getText();
        if (nameText.length() > 255) {
            lblNameError.setText("Name cannot exceed 255 characters."); // Set error message
            lblNameError.setForeground(Color.RED); // Set the color of the error message
        } else if (nameText.isEmpty()) {
            lblNameError.setText("Name cannot be empty."); // Optional: handle empty input
            lblNameError.setForeground(Color.RED); // Set the color of the error message
        } else {
            isNameValid = true;
            lblNameError.setText("Name is OK."); // Show success message
            lblNameError.setForeground(Color.GREEN); // Set the color of the success message
        }
        // Validate the original price field
        boolean isOriginalPriceValid = false;
        double originalPrice = txtOriginalPrice.getDoubleValue();
        if (originalPrice < 0) {
            lblOriginalPriceError.setText("Original price cannot be negative.");
            lblOriginalPriceError.setForeground(Color.RED);
        } else {
            isOriginalPriceValid = true;
            lblOriginalPriceError.setText("Original price is OK.");
            lblOriginalPriceError.setForeground(Color.GREEN);
        }
        // Validate the sale price field
        boolean isSalePriceValid = false;
        double salePrice = txtSalePrice.getDoubleValue();
        if (salePrice < originalPrice) {
            lblSalePriceError.setText("Sale price cannot be lower than original price.");
            lblSalePriceError.setForeground(Color.RED);
        } else {
            isSalePriceValid = true;
            lblSalePriceError.setText("Sale price is OK.");
            lblSalePriceError.setForeground(Color.GREEN);
        }

        // Validate the sizes table
        boolean isSizesValid = true;
        Set<String> sizeSet = new HashSet<>();
        boolean isUnique = true;

        for (int i = 0; i < sizeTableModel.getRowCount(); i++) {
            String size = sizeTableModel.getValueAt(i, 0).toString();
            String price = sizeTableModel.getValueAt(i, 1).toString();
            if (size.isEmpty()) {
                lblSizesError.setText("Size name cannot be empty.");
                lblSizesError.setForeground(Color.RED);
                isSizesValid = false;
                break;
            }

            // Check if size name exceeds max length
            if (size.length() > 50) {
                lblSizesError.setText("Size name cannot exceed 50 characters.");
                lblSizesError.setForeground(Color.RED);
                isSizesValid = false;
                break;
            }

            // Check if price exceeds max digits
            if (price.length() > 12) {
                lblSizesError.setText("Price cannot exceed 12 digits.");
                lblSizesError.setForeground(Color.RED);
                isSizesValid = false;
                break;
            }

            // Check if size name is unique
            if (!sizeSet.add(size)) {
                lblSizesError.setText("Size names must be unique.");
                lblSizesError.setForeground(Color.RED);
                isSizesValid = false;
                isUnique = false;
                break;
            }
        }

        // If all validations pass
        if (isUnique && isSizesValid) {
            lblSizesError.setText("Sizes are valid.");
            lblSizesError.setForeground(Color.GREEN);
        }

        // Validate the options table
        boolean isOptionsValid = true;
        // Check option name max length 50, max price 12 digits
        for (int i = 0; i < optionTableModel.getRowCount(); i++) {
            String option = optionTableModel.getValueAt(i, 0).toString();
            String price = optionTableModel.getValueAt(i, 1).toString();
            if (option.isEmpty()) {
                lblOptionsError.setText("Option name cannot be empty.");
                lblOptionsError.setForeground(Color.RED);
                isOptionsValid = false;
                break;
            } else if (option.length() > 50) {
                lblOptionsError.setText("Option name cannot exceed 50 characters.");
                lblOptionsError.setForeground(Color.RED);
                isOptionsValid = false;
                break;
            } else if (price.length() > 12) {
                lblOptionsError.setText("Price cannot exceed 12 digits.");
                lblOptionsError.setForeground(Color.RED);
                isOptionsValid = false;
                break;
            } else {
                lblOptionsError.setText("Options are valid.");
                lblOptionsError.setForeground(Color.GREEN);
                isOptionsValid = true;
            }
        }
        if (isOptionsValid) {
            lblOptionsError.setText("Options are valid.");
            lblOptionsError.setForeground(Color.green);
        }

        // Validate image cannot be null
        boolean isImageValid = false;
        if (lblImagePreview.getIcon() == null) {
            txtImagePath.setText("Image cannot be null.");
            txtImagePath.setForeground(Color.RED);
        } else {
            isImageValid = true;
            txtImagePath.setForeground(Color.GREEN);
        }

        // Validate description to long (>5000)
        boolean isDescriptionValid = false;
        if (txtDescription.getText().length() > 5000) {
            lblDescriptionError.setText("Description cannot exceed 5000 characters.");
            lblDescriptionError.setForeground(Color.RED);
        } else {
            isDescriptionValid = true;
            lblDescriptionError.setText("Description is OK.");
            lblDescriptionError.setForeground(Color.GREEN);
        }

        isInputsValid = isNameValid && isOriginalPriceValid && isSalePriceValid && isSizesValid && isOptionsValid
                && isImageValid && isDescriptionValid;

        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities.getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isInputsValid);
        }
        return isInputsValid;
    }

    /**
     * Returns an array of data representing the menu item details.
     * 
     * @return an array containing the menu item details
     */
    @Override
    public Object[] getData() {
        return new Object[] { txtName.getText(),
                MenuCategoryEnum.fromDisplayName(cmbCategory.getSelectedItem().toString()),
                txtOriginalPrice.getDoubleValue(), txtSalePrice.getDoubleValue(), getSizesFromTable(),
                getOptionsFromTable(), txtImagePath.getText(), txtDescription.getText() };
    }
}