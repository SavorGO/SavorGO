package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
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

	public static Option getInputFormDoubleSize() {
		Option option = new Option();
		option.getLayoutOption().setSize(0.5f, 2f).setLocation(Location.TRAILING, Location.TOP).setAnimateDistance(0.7f,
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

	public static JPanel createThumbnailPanel(ThumbnailCell thumbnail, boolean useMultithreading) {
		JPanel panel = new JPanel(new MigLayout("fillx", "[40][80][120][100][100][100][100][100][120][120]"));

		// Create labels for thumbnail, name, and status
		JLabel labelThumbnail = new JLabel();
		JLabel labelName = new JLabel();
		JLabel labelStatus = new JLabel();
		labelStatus.putClientProperty(FlatClientProperties.STYLE, "foreground:$Label.disabledForeground;");

		// If the image has not been loaded before
		if (thumbnail.getImageIcon() == null) {
			if (useMultithreading) {
				// Load the image in a separate thread
				ExecutorService executor = Executors.newSingleThreadExecutor();
				final ImageIcon[] iconHolder = new MyImageIcon[1]; // Use an array to hold the icon

				executor.submit(() -> {
					try {
						String imagePath = thumbnail.getImageUrl(); // Get the image path from thumbnail
						
						if ("table".equals(imagePath)) {
							iconHolder[0] = new MyImageIcon("src/main/resources/images/system/table.png", 55, 55, 10);
						} else {
							iconHolder[0] = MyImageIcon.getMyImageIconFromCloudinaryImageTag(imagePath, 55, 55, 10);
							if (iconHolder[0] == null) {
								iconHolder[0] = new MyImageIcon("src/main/resources/images/system/no_image_found.png",
										55, 55, 10);
							}
						}

						// Save the image to ThumbnailCell to avoid reloading
						thumbnail.setImageIcon(iconHolder[0]);

						// Ensure UI update happens on the EDT
						SwingUtilities.invokeLater(() -> {
							labelThumbnail.setIcon(iconHolder[0]);
							panel.revalidate(); // Ensure the panel is revalidated after UI change
							panel.repaint(); // Ensure the panel is repainted after update
						});

					} catch (Exception e) {
						e.printStackTrace();
						try {
							iconHolder[0] = new MyImageIcon("src/main/resources/images/system/no_image_found.png", 55,
									55, 10);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						// Ensure UI update happens on the EDT
						SwingUtilities.invokeLater(() -> {
							labelThumbnail.setIcon(iconHolder[0]);
							panel.revalidate(); // Ensure the panel is revalidated after UI change
							panel.repaint(); // Ensure the panel is repainted after update
						});
					}
				});
				executor.shutdown();
			} else {
				// Use single-threaded approach
				try {
					String imagePath = thumbnail.getImageUrl();
					ImageIcon icon;

					// Kiểm tra nếu imagePath là null hoặc rỗng
					if (imagePath == null || imagePath.isEmpty()) {
						icon = new MyImageIcon("src/main/resources/images/system/no_image_found.png", 55, 55, 10);
					} else {
						// Cố gắng tải hình ảnh từ Cloudinary
						icon = MyImageIcon.getMyImageIconFromCloudinaryImageTag(imagePath, 55, 55, 10);

						// Nếu không tải được hình ảnh, sử dụng hình ảnh "no image found"
						if (icon == null) {
							icon = new MyImageIcon("src/main/resources/images/system/no_image_found.png", 55, 55, 10);
						}
					}

					// Cập nhật icon cho thumbnail và label
					thumbnail.setImageIcon(icon);
					labelThumbnail.setIcon(icon);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			// If the image has been loaded, just set the icon from ThumbnailCell
			labelThumbnail.setIcon(thumbnail.getImageIcon());
		}

		// Set name and status
		labelName.setText(thumbnail.getName());
		String status = thumbnail.getStatus();
		labelStatus.setText(status);

		// Switch case to set color for different status
		switch (status) {
		case "Available":
			labelStatus.setForeground(Color.GREEN);
			break;
		case "Out of Service":
			labelStatus.setForeground(Color.RED);
			break;
		case "Occupied":
			labelStatus.setForeground(Color.YELLOW);
			break;
		case "Needs Cleaning":
			labelStatus.setForeground(Color.ORANGE);
			break;
		case "Deleted":
			labelStatus.setForeground(Color.BLACK);
			break;
		case "Discontinued":
			labelStatus.setForeground(Color.GRAY);
			break;
		case "Out of Stock":
			labelStatus.setForeground(Color.RED);
			break;
		default:
			labelStatus.setForeground(Color.GRAY);
			break;
		}

		// Add components to the panel
		panel.add(labelThumbnail, "span 1 2,w 55::,h 55::,grow 0");
		panel.add(labelName, "cell 1 0");
		panel.add(labelStatus, "cell 1 1");

		return panel;
	}

}
