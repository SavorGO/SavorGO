package raven.modal.demo.utils;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.datetime.TimePicker;
import raven.modal.option.Location;
import raven.modal.option.Option;

public class DefaultComponent {
	public static Option getInputForm() {
		Option option = new Option();
		option.getLayoutOption().setSize(-1, 1f).setLocation(Location.TRAILING, Location.TOP).setAnimateDistance(0.7f,
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
}
