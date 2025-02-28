package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.MenuStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.PromotionDiscountTypeEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.CustomFormattedTextField;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import lombok.val;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CreatePromotionInputForm extends PopupFormBasic<List<Object[]>> implements InputPopupForm {
    private List<String> menuIds;
    private JPanel promotionPanel;
    private JPanel listContainer;
    private List<PromotionRow> promotionRows = new ArrayList<>();
    private boolean isInputsValid = false;
    private JButton btnDeleteSelected;

    /**
     * Constructor for InputFormCreatePromotion.
     *
     * @param menuIds List of menu IDs to initialize the promotion form with.
     */
    public CreatePromotionInputForm(List<String> menuIds) {
        super();
        this.menuIds = menuIds;
        init();
    }

    @Override
    protected void init() {
        index = 1;
        createTitle();
        createFields();
        setViewportView(contentPanel);
        validateInput();
    }

    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Promotion Information");
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        contentPanel.add(lb, "gapy 5 0");
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0");
    }

    private Menu modelMenu;

    @Override
    protected void createFields() {
        // Add Thumbnail Panel with Add Button and ComboBox
        JLabel lblSelectMenu = new JLabel("Select menus to promote");
        lblSelectMenu.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblSelectMenu, "gapy 5 0");

        // ComboBox with PanelThumbnail Items
        JComboBox<Menu> comboBox = new JComboBox<>();
        // Set Custom Renderer for PanelThumbnail
        comboBox.setRenderer(new ListCellRenderer<Menu>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Menu> list, Menu value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    return new JLabel("No Menu Available"); // Fallback when value is null
                }
                // Display thumbnail in the combo box
                JPanel panel = DefaultComponent.createThumbnailPanel(menuController.getThumbnailCell(value), true);
                panel.setToolTipText(value.getName());
                panel.setOpaque(true);

                // Set background color for selected item
                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                } else {
                    panel.setBackground(list.getBackground());
                }

                // Ensure the panel has enough size for the image
                panel.setPreferredSize(new Dimension(300, 65));

                return panel;
            }
        });
        DefaultComboBoxModel<Menu> comboBoxModel = new DefaultComboBoxModel<>();
        comboBox.setModel(comboBoxModel);

        // Load menus synchronously
        List<Menu> availableMenus = getAvailableMenus();
        for (Menu menu : availableMenus) {
            comboBoxModel.addElement(menu);
        }

        // Add ComboBox to contentPanel
        contentPanel.add(comboBox, "growx, wrap");

        // Add Button for promotion
        JButton btnAdd = new JButton("Add menu for promotion");
        btnAdd.addActionListener(e -> addPromotionRow(((Menu) comboBox.getSelectedItem()).getId()));
        contentPanel.add(btnAdd, "growx, wrap");

        // Promotions Panel and Delete Button
        JPanel promotionsPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][pref]", ""));
        btnDeleteSelected = new JButton("Delete Selected");
        btnDeleteSelected.addActionListener(e -> deleteSelectedPromotions());
        JLabel lblListPromotion = new JLabel("List of Promotions");
        lblListPromotion.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblListPromotion, "gapy 5 0");
        promotionsPanel.add(btnDeleteSelected);
        contentPanel.add(promotionsPanel, "wrap");

        // List Container for promotions
        listContainer = new JPanel(new MigLayout("fillx, wrap 1", "[grow]"));
        JScrollPane scrollPane = new JScrollPane(listContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Set maximum height for JScrollPane
        contentPanel.add(scrollPane, "growy, pushy");

        // Add promotions (no threading needed here)
        if (menuIds != null) 
        for (String menuId : menuIds) {
            addPromotionRow(menuId);
        }

        validateInput();
    }

    	private List<Menu> getAvailableMenus() {
    		try {
    			ApiResponse apiResponse = menuController.list("", "name", "asc", 1, Integer.MAX_VALUE,
    					"available");

    			if (apiResponse.getErrors() != null) {
    				String errorMessage = apiResponse.getErrors().toString();
    				Toast.show(this, Toast.Type.ERROR, apiResponse.getMessage() + ":\n" + errorMessage);
    				return new ArrayList<>();
    			}

    			ObjectMapper objectMapper = new ObjectMapper();
    			objectMapper.registerModule(new JavaTimeModule());

    			// Ép kiểu data về Map<String, Object>
    			Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();

    			// Lấy danh sách data từ response
    			Object rawData = responseData.get("data");

    			// Chuyển rawData thành JSON string rồi parse thành List<Menu>
    			String jsonData = objectMapper.writeValueAsString(rawData);
    			return objectMapper.readValue(jsonData, new TypeReference<List<Menu>>() {
    			});

    		} catch (Exception e) {
    			System.err.println(e.getMessage());
    			Toast.show(this, Toast.Type.ERROR, "Failed to parse data: " + e.getMessage());
    			return new ArrayList<>();
    		}
    	}

    private static int index = 1;
    private MenuController menuController = new MenuController();

    private void addPromotionRow(String menuId) {
        Menu menu;
        try {
            ApiResponse apiResponse = menuController.getMenuById(menuId);

            if (apiResponse.getErrors() != null || apiResponse.getData() == null) {
                String errorMessage = apiResponse.getErrors() != null ? apiResponse.getErrors().toString() : "Unknown error";
                Toast.show(CreatePromotionInputForm.this, Toast.Type.ERROR, "Failed to fetch menu: " + errorMessage);
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Parse `data` từ API response thành đối tượng `Menu`
            String jsonData = objectMapper.writeValueAsString(apiResponse.getData());
            menu = objectMapper.readValue(jsonData, Menu.class);
        } catch (IOException e) {
            Toast.show(CreatePromotionInputForm.this, Toast.Type.ERROR, "Failed to get menu: " + e.getMessage());
            return;
        }

        PromotionRow row = new PromotionRow(menu, index++);
        row.setOnInputChangeListener(this::validateInput); // Set listener
        row.setPreferredSize(new Dimension(0, 100));
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.getComponent() instanceof PromotionRow) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        row.setSelected(!row.isSelected());
                        if (row.isSelected()) {
                            row.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green border
                        } else {
                            row.setBorder(BorderFactory.createLineBorder(Color.black, 5)); // Remove border when not selected
                        }
                    } else if (e.isPopupTrigger()) {
                        row.setSelected(true);
                        row.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green border
                        createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        promotionRows.add(row);
        listContainer.add(row, "growx");
        listContainer.revalidate();
        listContainer.repaint();
        validateInput();
    }


    /**
     * Creates a popup menu for the promotion row.
     *
     * @return The created JPopupMenu.
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem deleteMenuItem = new JMenuItem("Delete selected");
        popupMenu.add(deleteMenuItem);

        deleteMenuItem.addActionListener(e -> {
            deleteSelectedPromotions();
        });

        return popupMenu;
    }

    /**
     * Retrieves the indices of the selected promotion rows.
     *
     * @return List of indices of selected promotion rows.
     */
    private List<Integer> getSelectedTableIndex() {
        return promotionRows.stream().filter(PromotionRow::isSelected).map(PromotionRow::getIndex)
                .collect(Collectors.toList());
    }

    /**
     * Deletes the selected promotions from the list.
     */
    private void deleteSelectedPromotions() {
        if (getSelectedTableIndex().isEmpty()) {
            Toast.show(CreatePromotionInputForm.this, Toast.Type.ERROR, "Please select a promotion to delete");
            return;
        } else {
            Toast.show(CreatePromotionInputForm.this, Toast.Type.SUCCESS,
                    "Delete promotions index" + getSelectedTableIndex() + " successfully");
        }
        promotionRows.removeIf(row -> row.isSelected());
        listContainer.removeAll();
        for (PromotionRow row : promotionRows) {
            listContainer.add(row, "growx");
        }
        listContainer.revalidate();
        listContainer.repaint();
        validateInput();
    }

    private boolean isValidating = false;

    @Override
    public boolean validateInput() {
        if (isValidating) return false; // Prevent infinite loop
        isValidating = true; // Mark as validating

        isInputsValid = promotionRows.stream().allMatch(PromotionRow::validateInput);

        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
                .getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isInputsValid);
        }

        isValidating = false; // Mark as validation complete
        return isInputsValid;
    }

    private class PromotionRow extends JPanel {
        private JTextField txtOrdinalNumber = new JTextField();
        private JTextField txtId = new JTextField();
        private JPanel thumbnailPanel = new JPanel();
        private CustomFormattedTextField txtOriginalPrice = new CustomFormattedTextField();
        private CustomFormattedTextField txtSalePrice = new CustomFormattedTextField();
        private JComboBox<String> cmbDiscountType = new JComboBox<>(PromotionDiscountTypeEnum.getDisplayNames());
        private CustomFormattedTextField txtDiscountValue = new CustomFormattedTextField();
        private JLabel lblDiscountValueError = new JLabel();
        private JTextField txtName = new JTextField();
        private JLabel lblNameError = new JLabel();
        private CustomFormattedTextField txtDiscountedPrice = new CustomFormattedTextField();
        private JFormattedTextField txtStartDate = new JFormattedTextField();
        private DatePicker startDatePicker = DefaultComponent.getDatePicker(txtStartDate);
        private JLabel lblStartDateError = new JLabel();
        private JFormattedTextField txtEndDate = new JFormattedTextField();
        private DatePicker endDatePicker = DefaultComponent.getDatePicker(txtEndDate);
        private JLabel lblEndDateError = new JLabel();
        private boolean isSelected = false;
        private Menu menu;
        private int index;
        private Runnable inputChangeListener;

        /**
         * Sets the input change listener for the promotion row.
         *
         * @param listener The listener to be set.
         */
        public void setOnInputChangeListener(Runnable listener) {
            this.inputChangeListener = listener;
        }

        /**
         * Checks if the promotion row is selected.
         *
         * @return True if selected, false otherwise.
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Retrieves the index of the promotion row.
         *
         * @return The index of the promotion row.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Sets the selection state of the promotion row.
         *
         * @param isSelected The selection state to be set.
         */
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        /**
         * Constructor for PromotionRow.
         *
         * @param modelMenu The Menu object associated with this promotion row.
         * @param index     The index of the promotion row.
         */
        public PromotionRow(Menu modelMenu, int index) {
            setLayout(new MigLayout("fillx, wrap 2", "[right][grow]", ""));
            setBorder(BorderFactory.createLineBorder(Color.black, 5));
            this.menu = modelMenu;
            this.index = index;
            init();
            validateInput();
        }

        private void init() {
            add(new JLabel("#"));
            txtOrdinalNumber.setText(index + "");
            txtOrdinalNumber.setEditable(false);
            add(txtOrdinalNumber, "growx");

            add(new JLabel("ID"));
            txtId.setText(menu.getId());
            txtId.setEditable(false);
            add(txtId, "growx");

            add(new JLabel("Thumbnail"));
            thumbnailPanel = DefaultComponent.createThumbnailPanel(menuController.getThumbnailCell(menu), true);
            add(thumbnailPanel, "growx");

            add(new JLabel("Original Price"));
            add(txtOriginalPrice, "growx");
            txtOriginalPrice.setEditable(false);
            txtOriginalPrice.setValue(menu.getOriginalPrice());
            add(new JLabel("Sale Price"));
            add(txtSalePrice, "growx");
            txtSalePrice.setEditable(false);
            txtSalePrice.setValue(menu.getSalePrice());
            
            add(new JLabel("Name"));
            add(txtName, "growx");
            add(lblNameError, "span 2, growx");

            add(new JLabel("Discount Type"));
            add(cmbDiscountType, "growx");

            add(new JLabel("Discount Value"));
            add(txtDiscountValue, "growx");
            add(lblDiscountValueError, "span 2, growx");

            add(new JLabel("Discounted Price"));
            add(txtDiscountedPrice, "growx");
            txtDiscountedPrice.setEditable(false);

            double discountedPrice = 0;
            if (cmbDiscountType.getSelectedItem().equals(PromotionDiscountTypeEnum.PERCENTAGE)) {
                discountedPrice = menu.getSalePrice()
                        - (menu.getSalePrice() * txtDiscountValue.getDoubleValue() / 100);

            } else {
                discountedPrice = menu.getSalePrice() - txtDiscountValue.getDoubleValue();
            }

            if (discountedPrice < menu.getOriginalPrice()) {
                discountedPrice = menu.getOriginalPrice();
            }

            add(new JLabel("Start Date"));
            add(txtStartDate, "growx");
            add(lblStartDateError, "span 2, growx");

            add(new JLabel("End Date"));
            add(txtEndDate, "growx");
            add(lblEndDateError, "span 2, growx");

            // Add listeners
            txtName.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    super.keyReleased(e);
                    validateInput();
                }
            });
            txtOrdinalNumber.addPropertyChangeListener(evt -> validateInput());
            cmbDiscountType.addItemListener(evt -> validateInput());
            txtDiscountValue.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    validateInput();
                }
            });
            txtStartDate.addPropertyChangeListener(evt -> validateInput());
            txtEndDate.addPropertyChangeListener(evt -> validateInput());
        }

        private boolean isInputsValid = false;

        /**
         * Validates the input fields of the promotion row.
         *
         * @return True if all inputs are valid, false otherwise.
         */
        public boolean validateInput() {
            isInputsValid = false;
            // Validate the name
            boolean isNameValid = false;
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                lblNameError.setText("Name cannot be empty.");
                lblNameError.setForeground(Color.red);
            } else if (name.length() > 255) {
                lblNameError.setText("Name cannot be empty.");
                lblNameError.setForeground(Color.red);
            } else {
                lblNameError.setText("Name is valid.");
                lblNameError.setForeground(Color.green);
                isNameValid = true;
            }
            
            // Validate the discountValueValid
            boolean isDiscountValueValid = false;
            double discountValue = Math.ceil(txtDiscountValue.getDoubleValue());
            txtDiscountValue.setValue(discountValue);
            txtDiscountedPrice.setValue(0);
            if (discountValue <= 0) {
                lblDiscountValueError.setText("Discount value cannot be less than or equal to 0.");
                lblDiscountValueError.setForeground(Color.red);
                isDiscountValueValid = false;
            } else {
                String discountType = cmbDiscountType.getSelectedItem().toString();

                // Case for percentage discount (PERCENT)
                if (discountType.equals(PromotionDiscountTypeEnum.PERCENTAGE.getDisplayName())) {
                    if (discountValue > 100) {
                        lblDiscountValueError.setText("Discount value cannot be greater than 100%.");
                        lblDiscountValueError.setForeground(Color.red);
                    } else {
                        isDiscountValueValid = true;
                        // Check if percentage discount can reduce profit
                        double maxAllowedDiscount =  txtSalePrice.getDoubleValue() - txtOriginalPrice.getDoubleValue();
                        double discountAmount = txtSalePrice.getDoubleValue() * discountValue / 100;
                        
                        if (discountAmount > maxAllowedDiscount) {
                            txtDiscountedPrice.setValue(txtOriginalPrice.getValue());
                            lblDiscountValueError
                                    .setText("Discount value is not recommended to be higher than menu profit.");
                            lblDiscountValueError.setForeground(Color.orange);
                        } else {
                            txtDiscountedPrice.setValue(txtSalePrice.getDoubleValue() - discountAmount);
                            lblDiscountValueError.setText("Discount value is valid perfectly.");
                            lblDiscountValueError.setForeground(Color.green);
                        }
                    }
                }

                // Case for flat discount (FLAT)
                else if (discountType.equals(PromotionDiscountTypeEnum.FIXED_AMOUNT.getDisplayName())) {
                    double maxDiscount = txtSalePrice.getDoubleValue() - txtOriginalPrice.getDoubleValue();
                    isDiscountValueValid = true;
                    if (discountValue > maxDiscount) {
                        lblDiscountValueError
                                .setText("Discount value is not recommended to be higher than menu profit.");
                        lblDiscountValueError.setForeground(Color.orange);
                    } else {
                        lblDiscountValueError.setText("Discount value is valid perfectly.");
                        lblDiscountValueError.setForeground(Color.green);
                    }
                    txtDiscountedPrice.setValue(txtSalePrice.getDoubleValue() - discountValue);
                } else {
                    lblDiscountValueError.setText("Invalid discount type.");
                    lblDiscountValueError.setForeground(Color.red);
                    isDiscountValueValid = false;
                }
            }

            // Validate the startDate
            if (startDatePicker.getSelectedDate() == null) {
                lblStartDateError.setText("Start date is valid.");
                lblStartDateError.setForeground(Color.green);
            } else {
                LocalDate startDate = startDatePicker.getSelectedDate();
                if (startDate.isBefore(LocalDate.now())) {
                    lblStartDateError
                            .setText("Start date is not recommended to be before today. Empty is more recommended.");
                    lblStartDateError.setForeground(Color.orange);
                } else {
                    lblStartDateError.setText("Start date is valid perfectly.");
                    lblStartDateError.setForeground(Color.green);
                }
            }

            boolean isEndDateValid = false;
            if (endDatePicker.getSelectedDate() == null) {
                isEndDateValid = true;
                lblEndDateError.setText("End date is valid.");
                lblEndDateError.setForeground(Color.green);
            } else {
                LocalDate endDate = endDatePicker.getSelectedDate();
                if (endDate.isBefore(startDatePicker.getSelectedDate() == null ? LocalDate.now()
                        : startDatePicker.getSelectedDate())) {
                    lblEndDateError.setText(
                            "End date cannot be before start date or before today. Empty is more recommended.");
                    lblEndDateError.setForeground(Color.red);
                } else {
                    isEndDateValid = true;
                    lblEndDateError.setText("End date is valid perfectly.");
                    lblEndDateError.setForeground(Color.green);
                }
            }
            if (inputChangeListener != null)
                inputChangeListener.run();
            isInputsValid = isNameValid && isDiscountValueValid && isEndDateValid;
            return isInputsValid;
        }
    }

    @Override
    public List<Object[]> getData() {
        List<Object[]> promotionsData = new ArrayList<>();

        for (PromotionRow row : promotionRows) {
            if (row.validateInput()) { // Only collect data if the row is valid
                Object[] promotion = new Object[7]; // Assuming there are 6 fields to collect
                promotion[0] = row.txtId.getText(); // Menu ID
                promotion[1] = row.cmbDiscountType.getSelectedItem(); // Discount type
                promotion[2] = row.txtDiscountValue.getDoubleValue(); // Discount value
                promotion[3] = row.txtDiscountedPrice.getDoubleValue(); // Price after discount
                promotion[4] = row.startDatePicker.getSelectedDate(); // Start date
                promotion[5] = row.endDatePicker.getSelectedDate(); // End date
                promotion[6] = row.txtName.getText(); // Name
                promotionsData.add(promotion);
            }
        }
        return promotionsData; // Convert list to array
    }
}