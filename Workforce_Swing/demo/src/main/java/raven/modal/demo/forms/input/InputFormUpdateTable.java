package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import raven.modal.demo.controllers.ControllerTable;
import raven.modal.demo.models.EnumTableStatus;
import raven.modal.demo.models.ModelTable;
import raven.modal.demo.utils.BusinessException;
import raven.modal.demo.utils.DefaultComponent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.swing.*;

public class InputFormUpdateTable extends InputFormBasic<ModelTable> {

	private JTextField txtName; // Trường nhập liệu cho tên bàn
	private JComboBox<String> cmbStatus; // Trường chọn trạng thái
	private JFormattedTextField txtReservedTime; // Trường nhập thời gian đặt trước
	private JFormattedTextField txtReservedDate; // Trường nhập ngày đặt trước
	private ControllerTable controllerTable;
	private ModelTable modelTable;
	private DatePicker datePicker;
	private TimePicker timePicker;

	public InputFormUpdateTable(long id) throws IOException {
		super(); // Gọi constructor của lớp cha
		// Lấy dữ liệu từ API
		controllerTable = new ControllerTable();
		this.modelTable = controllerTable.getTableById(id);
		init();
	}

	@Override
	protected void init() {
		setLayout(new MigLayout("fillx,wrap,insets 5 30 5 30,width 400", "[fill]", ""));
		createTitle();
		// Trường nhập tên bàn
		txtName = new JTextField(modelTable.getName());
		txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Table Name");
		add(new JLabel("Table Name"), "gapy 5 0");
		add(txtName);

		// Trường chọn trạng thái từ enum nhưng trừ trường deleted
		String[] statuses = Arrays.stream(EnumTableStatus.values())
	    .filter(status -> status != EnumTableStatus.DELETED) // Lọc bỏ giá trị DELETED
	    .map(EnumTableStatus::getDisplayName)               // Lấy tên hiển thị
	    .toArray(String[]::new);                            // Chuyển sang mảng String[]
		cmbStatus = new JComboBox<>(statuses);
		cmbStatus.setSelectedItem(modelTable.getStatus().getDisplayName());
		cmbStatus.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Select Status");
		add(new JLabel("Status"), "gapy 5 0");
		add(cmbStatus);

		// Trường nhập thời gian đặt trước
		txtReservedTime = new JFormattedTextField();
		txtReservedTime.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
				"Reserved Time (e.g.,18:30)");
		txtReservedDate = new JFormattedTextField();
		txtReservedDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
				"Reserved Time (e.g.,18:30)");
        
        datePicker = DefaultComponent.getDatePicker(txtReservedDate);
        timePicker = DefaultComponent.getTimePicker(txtReservedTime);
        if(modelTable.getReservedTime() != null) {
            datePicker.setSelectedDate(modelTable.getReservedTime().toLocalDate());
    		timePicker.setSelectedTime(modelTable.getReservedTime().toLocalTime());
        }
		
		add(new JLabel("Reserved Time"), "gapy 5 0");
		JPanel panelTimePicker = new JPanel(new MigLayout());
		panelTimePicker.add(txtReservedDate, "width 150");
		panelTimePicker.add(txtReservedTime, "width 100");
		add(panelTimePicker);
	}

	@Override
	protected void createTitle() {
		JLabel lb = new JLabel("Table Information");
		lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
		add(lb, "gapy 5 0");
		add(new JSeparator(), "height 2!,gapy 0 0");
	}

	// Getter method to get the name entered by the user
	public String getTableName() {
		return txtName.getText();
	}

	// Getter method to get the reserved time entered by the user
	public LocalDateTime getReservedTime() throws BusinessException {
	    boolean isDateNull = datePicker.getSelectedDate() == null;
	    boolean isTimeNull = timePicker.getSelectedTime() == null;

	    // Kiểm tra điều kiện: Cả hai cùng null hoặc cả hai cùng khác null
	    if (isDateNull != isTimeNull) {
	        throw new BusinessException("Must select both date and time or leave both empty");
	    }

	    // Nếu cả hai là null, trả về null
	    if (isDateNull && isTimeNull) {
	        return null;
	    }

	    // Nếu cả hai không null, trả về LocalDateTime
	    return LocalDateTime.of(datePicker.getSelectedDate(), timePicker.getSelectedTime());
	}

	
	public EnumTableStatus getEnumTableStatus() {	
		return EnumTableStatus.fromDisplayName((String) cmbStatus.getSelectedItem());
	}
}
