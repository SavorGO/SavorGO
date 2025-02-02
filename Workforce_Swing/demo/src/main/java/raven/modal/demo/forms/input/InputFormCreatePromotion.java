package raven.modal.demo.forms.input;

import com.formdev.flatlaf.FlatClientProperties;

import lombok.val;
import net.miginfocom.swing.MigLayout;
import raven.datetime.DatePicker;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;
import raven.modal.demo.controllers.ControllerMenu;
import raven.modal.demo.forms.cards.CardMenu;
import raven.modal.demo.forms.info.PopupFormBasic;
import raven.modal.demo.models.EnumDiscountType;
import raven.modal.demo.models.EnumMenuStatus;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.utils.BusinessException;
import raven.modal.demo.utils.CustomFormattedTextField;
import raven.modal.demo.utils.DefaultComponent;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class InputFormCreatePromotion extends PopupFormBasic<List<Object[]>> {
	private List<String> menuIds;
	private JPanel promotionPanel;
	private JPanel listContainer;
	private List<PromotionRow> promotionRows = new ArrayList<>();
	private boolean isInputsValid = false;
	private JButton btnDeleteSelected;

	public InputFormCreatePromotion(List<String> menuIds) {
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

	private ModelMenu modelMenu;

	@Override
	protected void createFields() {
	    // Add Thumbnail Panel with Add Button and ComboBox
	    JLabel lblSelectMenu = new JLabel("Select menus to promote");
	    lblSelectMenu.putClientProperty(FlatClientProperties.STYLE, "font:bold");
	    contentPanel.add(lblSelectMenu, "gapy 5 0");

	    // ComboBox with PanelThumbnail Items
	    JComboBox<ModelMenu> comboBox = new JComboBox<>();
	    // Set Custom Renderer for PanelThumbnail
	    comboBox.setRenderer(new ListCellRenderer<ModelMenu>() {
	        @Override
	        public Component getListCellRendererComponent(JList<? extends ModelMenu> list, ModelMenu value, int index,
	                boolean isSelected, boolean cellHasFocus) {
	            if (value == null) {
	                return new JLabel("No Menu Available"); // Fallback when value is null
	            }
	            // Display thumbnail in the combo box
	            JPanel panel = DefaultComponent.createThumbnailPanel(value.getThumbnailCell(), true);
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
	    DefaultComboBoxModel<ModelMenu> comboBoxModel = new DefaultComboBoxModel<>();
	    comboBox.setModel(comboBoxModel);

	    // Load menus synchronously
	    List<ModelMenu> availableMenus = getAvailableMenus();
	    for (ModelMenu menu : availableMenus) {
	        comboBoxModel.addElement(menu);
	    }

	    // Add ComboBox to contentPanel
	    contentPanel.add(comboBox, "growx, wrap");

	    // Add Button for promotion
	    JButton btnAdd = new JButton("Add menu for promotion");
	    btnAdd.addActionListener(e -> addPromotionRow(((ModelMenu) comboBox.getSelectedItem()).getId()));
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

	    // Đặt chiều cao tối đa cho JScrollPane
	    contentPanel.add(scrollPane, "growy, pushy");

	    // Add promotions (no threading needed here)
	    if(menuIds != null) 
	    for (String menuId : menuIds) {
	        addPromotionRow(menuId);
	    }

	    // Validate input
	    validateInput();
	}

	private List<ModelMenu> getAvailableMenus() {
	    // This method returns only "AVAILABLE" menus, sorted by name, using parallel streams
	    try {
	        List<ModelMenu> allMenus = controllerMenu.getAllMenus(); // Fetch all menus synchronously
	        
	        // Sử dụng parallelStream để lọc và sắp xếp các menu có status là "AVAILABLE"
	        return allMenus.parallelStream()
	            .filter(menu -> EnumMenuStatus.AVAILABLE.equals(menu.getStatus()))  // Lọc các menu có status "AVAILABLE"
	            .sorted(Comparator.comparing(ModelMenu::getName))  // Sắp xếp theo tên
	            .collect(Collectors.toList());  // Thu thập kết quả vào một danh sách
	    } catch (IOException e) {
	        Toast.show(InputFormCreatePromotion.this, Toast.Type.ERROR, "Failed to get menus: " + e.getMessage());
	        return new ArrayList<>();  // Trả về danh sách rỗng nếu có lỗi
	    }
	}



	private static int index = 1;
	private ControllerMenu controllerMenu = new ControllerMenu();

	private void addPromotionRow(String menuId) {
		ModelMenu menu;
		try {
			menu = controllerMenu.getMenuById(menuId);
		} catch (IOException e) {
			Toast.show(InputFormCreatePromotion.this, Toast.Type.ERROR, "Failed to get menu: " + e.getMessage());
			return;
		}

		PromotionRow row = new PromotionRow(menu, index++);
		row.setOnInputChangeListener(this::validateInput); // Thiết lập listener
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
							row.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green
																							// border
						} else {
							row.setBorder(BorderFactory.createLineBorder(Color.black, 5)); // Remove border when not
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

	private JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem deleteMenuItem = new JMenuItem("Delete selected");
		popupMenu.add(deleteMenuItem);

		deleteMenuItem.addActionListener(e -> {
			deleteSelectedPromotions();
		});

		return popupMenu;
	}

	private List<Integer> getSelectedTableIndex() {
		return promotionRows.stream().filter(PromotionRow::isSelected).map(PromotionRow::getIndex)
				.collect(Collectors.toList());
	}

	private void deleteSelectedPromotions() {
		if (getSelectedTableIndex().isEmpty()) {
			Toast.show(InputFormCreatePromotion.this, Toast.Type.ERROR, "Please select a promotion to delete");
			return;
		} else {
			Toast.show(InputFormCreatePromotion.this, Toast.Type.SUCCESS,
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

	private void validateInput() {
	    if (isValidating) return; // Tránh vòng lặp vô hạn
	    isValidating = true; // Đánh dấu là đang kiểm tra

	    isInputsValid = promotionRows.stream().allMatch(PromotionRow::validateInput);

	    AdaptSimpleModalBorder modal = (AdaptSimpleModalBorder) SwingUtilities
	            .getAncestorOfClass(AdaptSimpleModalBorder.class, this);
	    if (modal != null) {
	        modal.setOkButtonEnabled(isInputsValid);
	    }

	    isValidating = false; // Đánh dấu là đã kiểm tra xong
	}

	private class PromotionRow extends JPanel {
		private JTextField txtOrdinalNumber = new JTextField();
		private JTextField txtId = new JTextField();
		private JPanel thumbnailPanel = new JPanel();
		private CustomFormattedTextField txtOriginalPrice = new CustomFormattedTextField();
		private CustomFormattedTextField txtSalePrice = new CustomFormattedTextField();
		private JComboBox<String> cmbDiscountType = new JComboBox<>(EnumDiscountType.getDisplayNames());
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
		private ModelMenu modelMenu;
		private int index;
		private Runnable inputChangeListener;

		public void setOnInputChangeListener(Runnable listener) {
			this.inputChangeListener = listener;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public int getIndex() {
			return index;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public PromotionRow(ModelMenu modelMenu, int index) {
			setLayout(new MigLayout("fillx, wrap 2", "[right][grow]", ""));
			setBorder(BorderFactory.createLineBorder(Color.black, 5));
			this.modelMenu = modelMenu;
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
			txtId.setText(modelMenu.getId());
			txtId.setEditable(false);
			add(txtId, "growx");

			add(new JLabel("Thumbnail"));
			thumbnailPanel = DefaultComponent.createThumbnailPanel(modelMenu.getThumbnailCell(), true);
			add(thumbnailPanel, "growx");

			add(new JLabel("Original Price"));
			add(txtOriginalPrice, "growx");
			txtOriginalPrice.setEditable(false);
			txtOriginalPrice.setValue(modelMenu.getOriginalPrice());
			add(new JLabel("Sale Price"));
			add(txtSalePrice, "growx");
			txtSalePrice.setEditable(false);
			txtSalePrice.setValue(modelMenu.getSalePrice());
			
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
			if (cmbDiscountType.getSelectedItem().equals(EnumDiscountType.PERCENT)) {
				discountedPrice = modelMenu.getSalePrice()
						- (modelMenu.getSalePrice() * txtDiscountValue.getDoubleValue() / 100);

			} else {
				discountedPrice = modelMenu.getSalePrice() - txtDiscountValue.getDoubleValue();
			}

			if (discountedPrice < modelMenu.getOriginalPrice()) {
				discountedPrice = modelMenu.getOriginalPrice();
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
						double maxAllowedDiscount =  txtSalePrice.getDoubleValue() - txtOriginalPrice.getDoubleValue();
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
	
	public List<Object[]> getPromotionData() {
	    List<Object[]> promotionsData = new ArrayList<>();

	    for (PromotionRow row : promotionRows) {
	        if (row.validateInput()) { // Chỉ lấy dữ liệu nếu hàng hợp lệ
	            Object[] promotion = new Object[7]; // Giả sử có 6 trường cần lấy
	            promotion[0] = row.txtId.getText(); // ID menu
	            promotion[1] = row.cmbDiscountType.getSelectedItem(); // Loại chiết khấu
	            promotion[2] = row.txtDiscountValue.getDoubleValue(); // Giá trị chiết khấu
	            promotion[3] = row.txtDiscountedPrice.getDoubleValue(); // Giá sau chiết khấu
	            promotion[4] = row.startDatePicker.getSelectedDate(); // Ngày bắt đầu
	            promotion[5] = row.endDatePicker.getSelectedDate(); // Ngày kết thúc
	            promotion[6] = row.txtName.getText(); // Ngày kết thúc
	            promotionsData.add(promotion);
	        }
	    }
	    return promotionsData; // Chuyển đổi danh sách thành mảng
	}
}
