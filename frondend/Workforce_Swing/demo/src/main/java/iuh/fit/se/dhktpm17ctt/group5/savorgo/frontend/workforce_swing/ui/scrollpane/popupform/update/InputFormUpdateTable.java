package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.TableController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.TableStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import raven.modal.component.AdaptSimpleModalBorder;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.swing.*;

public class InputFormUpdateTable extends PopupFormBasic<Table> implements InputPopupForm {
    private TableController tableController; // Controller to manage table data
    private Table modelTable; // Model representing the table
    private JTextField txtName; // Input field for table name
	private JLabel lblNameError = new JLabel(); // Declare lblNameError to access the value outside
    private JComboBox<String> cmbStatus = new JComboBox<>(TableStatusEnum.getDisplayNames()); // Dropdown for table status
    private JFormattedTextField txtReservedTime; // Input field for reserved time
    private JFormattedTextField txtReservedDate; // Input field for reserved date
	private JLabel lblReservedDateTimeError = new JLabel(); // Declare lblNameError to access the value outside
    private DatePicker datePicker; // Date picker component
    private TimePicker timePicker; // Time picker component
	private boolean isInputsValid = false; // Declare isInputsValid to access the value outside

    /** 
     * Constructor for InputFormUpdateTable.
     * Initializes the form and retrieves the table data by ID.
     * @param id The ID of the table to be updated.
     * @throws IOException If there is an error retrieving the table data.
     */
    public InputFormUpdateTable(long id) throws IOException {
        super(); // Call the constructor of the parent class
        tableController = new TableController(); // Initialize the controller
        this.modelTable = tableController.getTableById(id); // Retrieve the table data
        init(); // Initialize the form components
    }

    /** 
     * Initialize the form components.
     * This method sets up the layout and adds fields for table information.
     */
    @Override
    protected void init() {
        createTitle(); // Create the title for the form
        createFields(); // Create input fields for table information
        setViewportView(contentPanel); // Set the content panel as the viewport
        validateInput();
    }

    /** 
     * Create the title label for the form.
     * This method adds a title label and a separator to the content panel.
     */
    @Override
    protected void createTitle() {
        JLabel lb = new JLabel("Table Information"); // Create a label for the title
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2"); // Set the font style for the title
        contentPanel.add(lb, "gapy 5 0"); // Add the title label to the content panel
        contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
    }

    /** 
     * Create fields for the form.
     * This method is currently not implemented.
     */
    @Override
    protected void createFields() {
        // Input field for table name
        txtName = new JTextField(modelTable.getName());
        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Table Name");
        txtName.setColumns(255);
        txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				validateInput(); // Validate input on each key release
			}
		});
        JLabel lblName = new JLabel("Table Name");
		lblName.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        contentPanel.add(lblName, "gapy 5 0");
        contentPanel.add(txtName);

        // Error label for table name
        contentPanel.add(lblNameError); // Add the error label below the input field

        // Dropdown for table status, excluding the deleted status
        cmbStatus.setSelectedItem(modelTable.getStatus().getDisplayName());
        cmbStatus.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Select Status");
        contentPanel.add(new JLabel("Status"), "gapy 5 0");
        contentPanel.add(cmbStatus);

        // Input fields for reserved time and date
        txtReservedTime = new JFormattedTextField();
        txtReservedTime.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Reserved Time (e.g., 18:30)");
        txtReservedDate = new JFormattedTextField();
        txtReservedDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Reserved Date (e.g., 2023-10-01)");

        // Initialize date and time pickers
        datePicker = DefaultComponent.getDatePicker(txtReservedDate);
        timePicker = DefaultComponent.getTimePicker(txtReservedTime);

        // Set the selected date and time if available
        if (modelTable.getReservedTime() != null) {
            datePicker.setSelectedDate(modelTable.getReservedTime().toLocalDate());
            timePicker.setSelectedTime(modelTable.getReservedTime().toLocalTime());
        }
        
        txtReservedDate.addPropertyChangeListener(evt -> validateInput());
        txtReservedTime.addPropertyChangeListener(evt -> validateInput());

        // Add reserved time and date fields to a panel
        contentPanel.add(new JLabel("Reserved Time"), "gapy 5 0");
        JPanel panelTimePicker = new JPanel(new MigLayout());
        panelTimePicker.add(txtReservedDate, "width 150");
        panelTimePicker.add(txtReservedTime, "width 100");
        contentPanel.add(panelTimePicker);
        contentPanel.add(lblReservedDateTimeError); // Add the error label below the panel
    }

    /** 
     * Getter method to retrieve the reserved time entered by the user.
     * @return The LocalDateTime representing the reserved time.
     * @throws BusinessException If the date and time selection is invalid.
     * @throws IOException 
     */
    public LocalDateTime getReservedTime() throws BusinessException, IOException {
        boolean isDateNull = datePicker.getSelectedDate() == null;
        boolean isTimeNull = timePicker.getSelectedTime() == null;
        // Check if both date and time are either selected or not selected
        if (isDateNull != isTimeNull) {
            throw new BusinessException("Must select both date and time or leave both empty");
        }

        // If both are null, return null
        if (isDateNull && isTimeNull) {
            return null;
        }
        LocalDateTime reservedTime = LocalDateTime.of(datePicker.getSelectedDate(), timePicker.getSelectedTime());
        Table table = tableController.getTableById(modelTable.getId());
        if(!reservedTime.equals(table.getReservedTime()) && reservedTime.isAfter(LocalDateTime.now().plusDays(7))) throw new BusinessException("Cannot reserve after 7 days");
		if(!reservedTime.equals(table.getReservedTime()) && reservedTime.isBefore(LocalDateTime.now())) throw new BusinessException("Cannot change reserve time before now");
        // If both are not null, return LocalDateTime
        return reservedTime;
    }
    
    public boolean validateInput() {
    	isInputsValid = false;
    	boolean isNameValid = false;
        // Validate table name
        String nameText = txtName.getText().trim();
        if (nameText.isEmpty()) {
            lblNameError.setText("Table name cannot be empty.");
            lblNameError.setForeground(Color.RED);
            isNameValid = false;
        } else if (nameText.length() > 255) {
            lblNameError.setText("Table name cannot exceed 255 characters.");
			lblNameError.setForeground(Color.RED); // Set the color of the error messag
            isNameValid = false;
        } else {
			lblNameError.setText("Name is valid."); // Show success message
			lblNameError.setForeground(Color.GREEN); // Set the color of the success message
            isNameValid = true;
        }

        // Validate reserved date and time
        boolean isReservedDateTimeValid = false;
		try {
			getReservedTime();
			lblReservedDateTimeError.setText("Reserved date and time are valid.");
			isReservedDateTimeValid = true;
			lblReservedDateTimeError.setForeground(Color.GREEN);
		} catch (BusinessException | IOException e) {
			lblReservedDateTimeError.setText(e.getMessage());
			lblReservedDateTimeError.setForeground(Color.RED);
			isReservedDateTimeValid = false;
		}
		
		isInputsValid = isNameValid && isReservedDateTimeValid;
        // Update modal OK button state
        AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities.getAncestorOfClass(AdaptSimpleModalBorder.class, this);
        if (modal != null) {
            modal.setOkButtonEnabled(isInputsValid);
        }
        return isInputsValid;
    }
    @Override
	public Object[] getData() throws BusinessException, IOException {
		return new Object[] { modelTable.getId(), txtName.getText(), TableStatusEnum.fromDisplayName(cmbStatus.getSelectedItem().toString()) , getReservedTime() };
	}
}