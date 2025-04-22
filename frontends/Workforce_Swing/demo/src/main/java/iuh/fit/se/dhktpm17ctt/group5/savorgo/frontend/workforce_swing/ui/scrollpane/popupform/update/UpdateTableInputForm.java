package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.TableController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.TableStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.InputPopupForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.PopupFormBasic;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import javax.swing.*;

public class UpdateTableInputForm extends PopupFormBasic<Table> implements InputPopupForm {
	private TableController tableController; // Controller to manage table data
	private Table modelTable; // Model representing the table
	private JTextField txtName; // Input field for table name
	private JLabel lblNameError = new JLabel(); // Declare lblNameError to access the value outside
	private JComboBox<String> cmbStatus = new JComboBox<>(TableStatusEnum.getDisplayNames()); // Dropdown for table
																								// status
	private JFormattedTextField txtReservedTime; // Input field for reserved time
	private JFormattedTextField txtReservedDate; // Input field for reserved date
	private JLabel lblReservedDateTimeError = new JLabel(); // Declare lblNameError to access the value outside
	private DatePicker datePicker; // Date picker component
	private TimePicker timePicker; // Time picker component
	private boolean isInputsValid = false; // Declare isInputsValid to access the value outside

	public UpdateTableInputForm(	long id)throws IOException
	{
		tableController = new TableController();
		ApiResponse apiResponse = tableController.getTableById(id);

		if (apiResponse.getErrors() != null || apiResponse.getData() == null) {
			String errorMessage = apiResponse.getErrors() != null ? apiResponse.getErrors().toString()
					: "Unknown error";
			Toast.show(this, Toast.Type.ERROR, "Failed to fetch table: " + errorMessage);
			return;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		// Parse `data` từ API response thành đối tượng `Table`
		String jsonData = objectMapper.writeValueAsString(apiResponse.getData());
		modelTable = objectMapper.readValue(jsonData, Table.class);

		init();
	}

	@Override
	protected void init() {
		createTitle(); // Create the title for the form
		createFields(); // Create input fields for table information
		setViewportView(contentPanel); // Set the content panel as the viewport
		validateInput();
	}

	@Override
	protected void createTitle() {
		JLabel lb = new JLabel("Table Information"); // Create a label for the title
		lb.putClientProperty(FlatClientProperties.STYLE, "font:+2"); // Set the font style for the title
		contentPanel.add(lb, "gapy 5 0"); // Add the title label to the content panel
		contentPanel.add(new JSeparator(), "height 2!,gapy 0 0"); // Add a separator below the title
	}

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
		ApiResponse apiResponse = tableController.getTableById(modelTable.getId());

		if (apiResponse.getErrors() != null || apiResponse.getData() == null) {
			String errorMessage = apiResponse.getErrors() != null ? apiResponse.getErrors().toString()
					: "Unknown error";
			Toast.show(this, Toast.Type.ERROR, "Failed to fetch table: " + errorMessage);
			return null;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		// Parse `data` từ API response thành đối tượng `Table`
		String jsonData = objectMapper.writeValueAsString(apiResponse.getData());
		Table table = objectMapper.readValue(jsonData, Table.class);
		if (!reservedTime.equals(table.getReservedTime()) && reservedTime.isAfter(LocalDateTime.now().plusDays(7)))
			throw new BusinessException("Cannot reserve after 7 days");
		if (!reservedTime.equals(table.getReservedTime()) && reservedTime.isBefore(LocalDateTime.now()))
			throw new BusinessException("Cannot change reserve time before now");
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
		AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
				.getAncestorOfClass(AdaptSimpleModalBorder.class, this);
		if (modal != null) {
			modal.setOkButtonEnabled(isInputsValid);
		}
		return isInputsValid;
	}

	@Override
	public Object[] getData() throws BusinessException, IOException {
		return new Object[] { modelTable.getId(), txtName.getText(),
				TableStatusEnum.fromDisplayName(cmbStatus.getSelectedItem().toString()), getReservedTime() };
	}
}