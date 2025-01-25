package raven.modal.demo.utils;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import raven.modal.option.Location;
import raven.modal.option.Option;

public class DefaultComponent {
	public static Option getInfoForm() {
		Option option = new Option();
		option.getLayoutOption().setSize(-1, 0.8f).setLocation(Location.CENTER, Location.CENTER)
				.setAnimateDistance(0.7f, 0);
		return option;
	}

	public static Option getInputForm() {
		Option option = new Option();
		option.getLayoutOption().setSize(-1, 2f).setLocation(Location.TRAILING, Location.TOP).setAnimateDistance(0.7f,
				0);
		return option;
	}

	public static Option getChoiceModal() {
		Option option = new Option();
		option.getLayoutOption().setSize(-1, 0.3f).setLocation(Location.CENTER, Location.CENTER)
				.setAnimateDistance(0.7f, 0);
		return option;
	}

	public static Option getToast() {
		Option option = new Option();
		option.getLayoutOption().setSize(-1, 0.3f).setLocation(Location.CENTER, Location.CENTER)
				.setAnimateDistance(0.7f, 0);
		return option;
	}

	public static DatePicker getDatePicker(JFormattedTextField dateEditor) {
		DatePicker datePicker = new DatePicker();
		// Thiết lập thông số mặc định
		datePicker.setUsePanelOption(true); // Bật tùy chọn panel
		datePicker.setAnimationEnabled(true); // Bật hiệu ứng
		datePicker.setEditor(dateEditor);
		return datePicker;
	}

	public static TimePicker getTimePicker(JFormattedTextField timeEditor) {
		TimePicker timePicker = new TimePicker();
		timePicker.set24HourView(true);
		timePicker.setEditor(timeEditor);
		return timePicker;
	}

	public static JFormattedTextField getFormattedTextField() {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(2); // Số thập phân tối đa
		JFormattedTextField formattedTextField = new JFormattedTextField(numberFormat);
		formattedTextField.setColumns(12);
		formattedTextField.setValue(0.0d);
		formattedTextField.setHorizontalAlignment(JFormattedTextField.RIGHT);
		formattedTextField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (formattedTextField.getText().trim().isEmpty()) {
					formattedTextField.setValue(0.0); // Gán lại giá trị mặc định
				}
			}
		});
		return formattedTextField;
	}
}
