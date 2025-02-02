package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.modal.component.AdaptSimpleModalBorder;
import raven.modal.demo.forms.info.PopupFormBasic;
import raven.modal.demo.models.EnumDiscountType;
import raven.modal.demo.models.EnumStatusPromotion;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.models.ModelPromotion;
import raven.modal.demo.utils.CustomFormattedTextField;
import raven.modal.demo.utils.DefaultComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

public class InputFormUpdatePromotion extends PopupFormBasic<ModelPromotion> {
	private ModelPromotion modelPromotion; // The promotion model to update
	private ModelMenu modelMenu;

	// Các control được khởi tạo ngay tại phần khai báo
	private JTextField txtName = new JTextField();
	private JTextField txtThumbnail = new JTextField();
	private CustomFormattedTextField txtOriginalPrice = new CustomFormattedTextField();
	private CustomFormattedTextField txtSalePrice = new CustomFormattedTextField();
	private JComboBox<String> cmbDiscountType = new JComboBox<>(new String[] { "Percent", "Flat" });
	private CustomFormattedTextField txtDiscountValue = new CustomFormattedTextField();
	private CustomFormattedTextField txtDiscountedPrice = new CustomFormattedTextField();

	// Các biến cho ngày: sử dụng JFormattedTextField kết hợp với DatePicker
	private JFormattedTextField txtStartDate = new JFormattedTextField();
	private DatePicker startDatePicker = DefaultComponent.getDatePicker(txtStartDate);
	private JFormattedTextField txtEndDate = new JFormattedTextField();
	private DatePicker endDatePicker = DefaultComponent.getDatePicker(txtEndDate);

	// Error labels (đã set màu đỏ mặc định)
	private JLabel lblNameError = new JLabel();
	private JLabel lblDiscountValueError = new JLabel();
	private JLabel lblStartDateError = new JLabel();
	private JLabel lblEndDateError = new JLabel();

	public InputFormUpdatePromotion(ModelPromotion promotion, ModelMenu menu) {
		super();
		this.modelPromotion = promotion;
		this.modelMenu = menu;
		init();
	}

	@Override
	protected void init() {
		createTitle();
		createFields();
		setViewportView(contentPanel);
		setValue(); // Populate fields with the promotion data
		validateInput();
	}

	@Override
	protected void createTitle() {
		JLabel lb = new JLabel("Update Promotion Information");
		lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
		// Dùng gapy cho khoảng cách giữa tiêu đề và nội dung bên dưới
		contentPanel.add(lb, "gapy 5 0, span, wrap");
		contentPanel.add(new JSeparator(), "height 2!, gapy 0 0, span, wrap");
	}

	@Override
	protected void createFields() {
		if (modelMenu.getThumbnailCell() != null) {
			// Hiển thị panel thumbnail chiếm 3 cột, cách dòng 5px
			contentPanel.add(DefaultComponent.createThumbnailPanel(modelMenu.getThumbnailCell(), true),
					"span 3, gapy 5 0, wrap");
		}

		// --- Original Price ---
		JLabel lblOriginalPrice = new JLabel("Original Price:");
		lblOriginalPrice.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblOriginalPrice, "align label");
		contentPanel.add(txtOriginalPrice, "growx");
		txtOriginalPrice.setEditable(false);

		// --- Sale Price ---
		JLabel lblSalePrice = new JLabel("Sale Price:");
		lblSalePrice.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblSalePrice, "align label");
		contentPanel.add(txtSalePrice, "growx");
		txtSalePrice.setEditable(false);

		// --- Name ---
		JLabel lblName = new JLabel("Name:");
		lblName.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblName, "align label");
		contentPanel.add(txtName, "growx");
		contentPanel.add(lblNameError, "growx,wrap");

		// --- Discount Type ---
		JLabel lblDiscountType = new JLabel("Discount Type:");
		lblDiscountType.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblDiscountType, "align label");
		contentPanel.add(cmbDiscountType, "growx");
		contentPanel.add(new JLabel(""), "wrap");

		// --- Discount Value ---
		JLabel lblDiscountValue = new JLabel("Discount Value:");
		lblDiscountValue.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblDiscountValue, "align label");
		contentPanel.add(txtDiscountValue, "growx");
		contentPanel.add(lblDiscountValueError, "growx,wrap");

		// --- Discounted Price ---
		JLabel lblDiscountedPrice = new JLabel("Discounted Price:");
		lblDiscountedPrice.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		txtDiscountedPrice.setEditable(false);
		contentPanel.add(lblDiscountedPrice, "align label");
		contentPanel.add(txtDiscountedPrice, "growx");
		contentPanel.add(new JLabel(""), "wrap");

		// --- Start Date ---
		JLabel lblStartDate = new JLabel("Start Date:");
		lblStartDate.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblStartDate, "align label");
		contentPanel.add(txtStartDate, "growx");
		lblStartDateError.setForeground(Color.RED);
		contentPanel.add(lblStartDateError, "growx,wrap");

		// --- End Date ---
		JLabel lblEndDate = new JLabel("End Date:");
		lblEndDate.putClientProperty(FlatClientProperties.STYLE, "font:bold");
		contentPanel.add(lblEndDate, "align label");
		contentPanel.add(txtEndDate, "growx");
		lblEndDateError.setForeground(Color.RED);
		contentPanel.add(lblEndDateError, "growx,wrap");

		// Add listeners
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				validateInput();
			}
		});
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

	private void setValue() {
		// Gán giá trị từ modelPromotion và modelMenu cho các control tương ứng
		txtName.setText(modelPromotion.getName());
		txtOriginalPrice.setValue(modelMenu.getOriginalPrice());
		txtSalePrice.setValue(modelMenu.getSalePrice());
		cmbDiscountType.setSelectedItem(modelPromotion.getDiscountType().getDisplayName());
		txtDiscountValue.setValue(modelPromotion.getDiscountValue());

		// Nếu có giá trị, gán ngày bắt đầu và kết thúc; nếu null thì control giữ trống
		if (modelPromotion.getStartDate() != null) {
			startDatePicker.setSelectedDate(modelPromotion.getStartDate());
		}
		if (modelPromotion.getEndDate() != null) {
			endDatePicker.setSelectedDate(modelPromotion.getEndDate());
		}
	}

	private boolean isInputsValid = false;

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
		double discountValue = txtDiscountValue.getDoubleValue();
		txtDiscountedPrice.setValue(0);
		if (discountValue <= 0) {
			lblDiscountValueError.setText("Discount value cannot be less than or equal to 0.");
			lblDiscountValueError.setForeground(Color.red);
			isDiscountValueValid = false;
		} else {
			String discountType = cmbDiscountType.getSelectedItem().toString();

			// Trường hợp chiết khấu theo phần trăm (PERCENT)
			if (discountType.equals(EnumDiscountType.PERCENT.getDisplayName())) {
				if (discountValue > 100) {
					lblDiscountValueError.setText("Discount value cannot be greater than 100%.");
					lblDiscountValueError.setForeground(Color.red);
				} else {
					isDiscountValueValid = true;
					// Kiểm tra nếu chiết khấu phần trăm có thể làm giảm lợi nhuận
					double maxAllowedDiscount = txtSalePrice.getDoubleValue() - txtOriginalPrice.getDoubleValue();
					double discountAmount = txtSalePrice.getDoubleValue() * discountValue / 100;
					txtDiscountedPrice.setValue(discountAmount);
					if (discountAmount > maxAllowedDiscount) {
						lblDiscountValueError
								.setText("Discount value is not recommended to be higher than menu profit.");
						lblDiscountValueError.setForeground(Color.orange);
					} else {
						lblDiscountValueError.setText("Discount value is valid perfectly.");
						lblDiscountValueError.setForeground(Color.green);
					}
				}
			}

			// Trường hợp chiết khấu theo số tiền cụ thể (FLAT)
			else if (discountType.equals(EnumDiscountType.FLAT.getDisplayName())) {
				double maxDiscount = txtSalePrice.getDoubleValue() - txtOriginalPrice.getDoubleValue();
				isDiscountValueValid = true;
				if (discountValue > maxDiscount) {
					lblDiscountValueError.setText("Discount value is not recommended to be higher than menu profit.");
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
			if (endDate.isBefore(
					startDatePicker.getSelectedDate() == null ? LocalDate.now() : startDatePicker.getSelectedDate())) {
				lblEndDateError
						.setText("End date cannot be before start date or before today. Empty is more recommended.");
				lblEndDateError.setForeground(Color.red);
			} else {
				isEndDateValid = true;
				lblEndDateError.setText("End date is valid perfectly.");
				lblEndDateError.setForeground(Color.green);
			}
		}

		isInputsValid = isNameValid && isDiscountValueValid && isEndDateValid;
		AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities.getAncestorOfClass(AdaptSimpleModalBorder.class, this);
		if (modal != null) {
			modal.setOkButtonEnabled(isInputsValid);
		}
		return isInputsValid;
	}

	public Object[] getPromotionData() {
		return new Object[] {(long) modelPromotion.getId(), txtName.getText(), EnumDiscountType.fromDisplayName(cmbDiscountType.getSelectedItem().toString()), txtDiscountValue.getDoubleValue(), startDatePicker.getSelectedDate(), endDatePicker.getSelectedDate() };
	}
}
