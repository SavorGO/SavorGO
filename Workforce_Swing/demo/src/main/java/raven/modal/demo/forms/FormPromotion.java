package raven.modal.demo.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;
import raven.modal.demo.controllers.ControllerMenu;
import raven.modal.demo.controllers.ControllerPromotion;
import raven.modal.demo.controllers.ControllerTable;
import raven.modal.demo.forms.cards.CardPromotion;
import raven.modal.demo.forms.cards.CardTable;
import raven.modal.demo.forms.info.InfoFormPromotion;
import raven.modal.demo.forms.info.InfoFormTable;
import raven.modal.demo.forms.input.InputFormCreatePromotion;
import raven.modal.demo.forms.input.InputFormCreateTable;
import raven.modal.demo.forms.input.InputFormUpdatePromotion;
import raven.modal.demo.forms.input.InputFormUpdateTable;
import raven.modal.demo.layout.ResponsiveLayout;
import raven.modal.demo.models.EnumMenuStatus;
import raven.modal.demo.models.EnumStatusPromotion;
import raven.modal.demo.models.EnumTableStatus;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.models.ModelProfile;
import raven.modal.demo.models.ModelPromotion;
import raven.modal.demo.services.ServiceTable;
import raven.modal.demo.services.impls.ServiceImplTable;
import raven.modal.demo.simple.SimpleMessageModal;
import raven.modal.demo.system.Form;
import raven.modal.demo.utils.BusinessException;
import raven.modal.demo.utils.DefaultComponent;
import raven.modal.demo.utils.SystemForm;
import raven.modal.demo.utils.table.CheckBoxTableHeaderRenderer;
import raven.modal.demo.utils.table.TableHeaderAlignment;
import raven.modal.demo.utils.table.TableProfileCellRenderer;
import raven.modal.demo.utils.table.TableThumbnailRenderer;
import raven.modal.demo.utils.table.ThumbnailCell;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

@SystemForm(name = "Promotion", description = "Promtion is a user interface component", tags = { "list" })
public class FormPromotion extends Form {
	private final Object columns[] = new Object[] { "SELECT", "#", "THUMBNAIL", "VALUE", "START DATE", "END DATE",
			"MENU THUMBNAIL", "ORIGINAL PRICE", "SALE PRICE", "DISCOUNTED PRICE", "CREATED TIME", "UPDATED TIME" };
	private DefaultTableModel tableModel = createTableModel(); // Table model for
	private JTable table = new JTable(tableModel);
	private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT,
			new Dimension(-1, -1), 10, 10);
	private JPanel panelCard = new JPanel(responsiveLayout);
	private static final int DEBOUNCE_DELAY = 1000; // Debounce delay in milliseconds
	private Timer debounceTimer;
	private String selectedTitle = "Basic table"; // Selected table title for";
	private ControllerPromotion controllerPromotion = new ControllerPromotion();

	public FormPromotion() {
		init();
	}

	private void init() {
		setLayout(new MigLayout("fillx,wrap", "[fill]", "[][fill,grow]"));
		add(createInfoPanel("Promotion Management",
				"This is a user interface component that displays a collection of promotions in a structured, tabular format. It allows users to view, sort, and manage data or other resources.",
				1));
		add(createTabPanel(), "gapx 7 7");
	}

	private JPanel createInfoPanel(String title, String description, int level) {
		JPanel panel = new JPanel(new MigLayout("fillx,wrap", "[fill]"));
		JLabel lbTitle = new JLabel(title);
		JTextPane text = new JTextPane();
		text.setText(description);
		text.setEditable(false);
		text.setBorder(BorderFactory.createEmptyBorder());
		lbTitle.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +" + (4 - level));
		panel.add(lbTitle);
		panel.add(text, "width 500");
		return panel;
	}

	private Component createTabPanel() {
		JTabbedPane tabb = new JTabbedPane();
		tabb.putClientProperty(FlatClientProperties.STYLE, "" + "tabType:card");
		tabb.addTab("Basic table", createBorder(createBasicTable()));
		tabb.addTab("Grid table", createBorder(createGridTable()));
		tabb.addChangeListener(e -> {
			loadData("");
			selectedTitle = tabb.getTitleAt(tabb.getSelectedIndex());
		});
		return tabb;
	}

	private Component createBorder(Component component) {
		JPanel panel = new JPanel(new MigLayout("fill,insets 7 0 7 0", "[fill]", "[fill]"));
		panel.add(component);
		return panel;
	}

	private Component createBasicTable() {
		JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
		configureTableProperties();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		styleTableWithFlatLaf(scrollPane);
		panel.add(createTableTitle("Basic Table"), "gapx 20");
		panel.add(createHeaderActionPanel());
		panel.add(scrollPane);
		loadData("");
		return panel;
	}

	private void configureTableProperties() {
		table.setModel(tableModel);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0)); // Không có khoảng cách giữa các ô
		table.setRowHeight(50);
		table.setSelectionBackground(new Color(220, 230, 241));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setReorderingAllowed(true);
		table.getColumnModel().getColumn(0).setMaxWidth(50); // Cột SELECT
		table.getColumnModel().getColumn(1).setMaxWidth(50); // Cột #
		table.getColumnModel().getColumn(2).setMinWidth(200); // Cột THUMBNAIL
		table.getColumnModel().getColumn(3).setPreferredWidth(100); // Cột VALUE
		table.getColumnModel().getColumn(4).setPreferredWidth(250); // Cột START DATE
		table.getColumnModel().getColumn(5).setPreferredWidth(250); // Cột END DATE
		table.getColumnModel().getColumn(6).setMinWidth(200); // Cột MENU THUMBNAIL
		table.getColumnModel().getColumn(7).setPreferredWidth(150); // Cột ORIGINAL PRICE
		table.getColumnModel().getColumn(8).setPreferredWidth(150); // Cột SALE PRICE
		table.getColumnModel().getColumn(9).setPreferredWidth(150); // Cột DISCOUNTED PRICE
		table.getColumnModel().getColumn(10).setPreferredWidth(200); // Cột CREATED TIME
		table.getColumnModel().getColumn(11).setPreferredWidth(200); // Cột UPDATED TIME

		// Thiết lập renderer cho cột có kiểu dữ liệu đặc biệt (ví dụ: hình thu nhỏ)
		table.setDefaultRenderer(ThumbnailCell.class, new TableThumbnailRenderer(table));

		// Thiết lập header renderer cho cột "SELECT"
		table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));

		// Thiết lập canh lề của các cột
		table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
			@Override
			protected int getAlignment(int column) {
				// Các cột cần canh phải (trailing)
				int trailing[] = { 4, 5, 7, 8, 9, 10, 11 }; // Cột giá trị và ngày tháng
				// Các cột cần canh giữa (center)
				int center[] = { 2, 6 }; // Cột thumbnail

				// Kiểm tra nếu column nằm trong trailing (canh phải)
				if (Arrays.stream(trailing).anyMatch(value -> value == column)) {
					return SwingConstants.TRAILING;
				}
				// Kiểm tra nếu column nằm trong center (canh giữa)
				if (Arrays.stream(center).anyMatch(value -> value == column)) {
					return SwingConstants.CENTER;
				}
				// Các cột còn lại canh trái (leading)
				return SwingConstants.LEADING;
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					int row = table.rowAtPoint(e.getPoint());
					if (!table.isRowSelected(row)) {
						table.setRowSelectionInterval(row, row);
					}
					table.setValueAt(true, row, 0);
					createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	private void styleTableWithFlatLaf(JScrollPane scrollPane) {
		panelCard.putClientProperty(FlatClientProperties.STYLE, "" + "arc:20;" + "background:$Table.background;");
		table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "" + "height:30;" + "hoverBackground:null;"
				+ "pressedBackground:null;" + "separatorColor:$TableHeader.background;");
		table.putClientProperty(FlatClientProperties.STYLE,
				"" + "rowHeight:70;" + "showHorizontalLines:true;" + "intercellSpacing:0,1;"
						+ "cellFocusColor:$TableHeader.hoverBackground;"
						+ "selectionBackground:$TableHeader.hoverBackground;"
						+ "selectionInactiveBackground:$TableHeader.hoverBackground;"
						+ "selectionForeground:$Table.foreground;");
		scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
				"" + "trackArc:$ScrollBar.thumbArc;" + "trackInsets:3,3,3,3;" + "thumbInsets:3,3,3,3;"
						+ "background:$Table.background;");
	}

	private JLabel createTableTitle(String title) {
		JLabel titleLabel = new JLabel(title);
		titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
		return titleLabel;
	}

	private Component createGridTable() {
		JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
		configurePanelCardStyle();
		JScrollPane scrollPane = createScrollPaneForPanelCard();
		panel.add(createTableTitle("Table Grid Table"), "gapx 20");
		panel.add(createHeaderActionPanel());
		panel.add(scrollPane);
		loadData("");
		return panel;
	}

	/**
	 * Configures the style of the panel card.
	 */
	private void configurePanelCardStyle() {
		panelCard.putClientProperty(FlatClientProperties.STYLE, "border:10,10,10,10;");
	}

	/**
	 * Creates a JScrollPane for the panel card.
	 * 
	 * @return The created JScrollPane.
	 */
	private JScrollPane createScrollPaneForPanelCard() {
		JScrollPane scrollPane = new JScrollPane(panelCard);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,
				"trackArc:$ScrollBar.thumbArc; thumbInsets:0,0,0,0; width:5;");
		scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
				"trackArc:$ScrollBar.thumbArc; thumbInsets:0,0,0,0; width:5;");
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		return scrollPane;
	}

	/**
	 * Creates the header action panel containing buttons and search field.
	 * 
	 * @return The created JPanel for header actions.
	 */
	private Component createHeaderActionPanel() {
		JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill,230]push[][]"));
		JTextField txtSearch = createSearchTextField();
		JButton cmdDetails = createButton("Details", e -> showModal("details"));
		JButton cmdCreate = createButton("Create", e -> showModal("create"));
		JButton cmdEdit = createButton("Edit", e -> showModal("edit"));
		JButton cmdDelete = createButton("Delete", e -> showModal("delete"));
		panel.add(txtSearch);
		panel.add(cmdDetails);
		panel.add(cmdCreate);
		panel.add(cmdEdit);
		panel.add(cmdDelete);
		panel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
		return panel;
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem detailsMenuItem = new JMenuItem("View Details");
		JMenuItem copyMenuItem = new JMenuItem("Copy cell text");
		JMenuItem editMenuItem = new JMenuItem("Edit");
		JMenuItem deleteMenuItem = new JMenuItem(
				(findSelectedPromotionIds().size() == 1 || (findSelectedPromotionIds(panelCard).size() == 1)) ? "Delete"
						: "Delete many");
		;
		popupMenu.add(detailsMenuItem);
		popupMenu.add(copyMenuItem);
		popupMenu.add(editMenuItem);
		popupMenu.add(deleteMenuItem);

		detailsMenuItem.addActionListener(e -> {
			showModal("details");
		});

		copyMenuItem.addActionListener(e -> copyAction());

		editMenuItem.addActionListener(e -> {
			showModal("edit");
		});

		deleteMenuItem.addActionListener(e -> {
			showModal("delete");
		});

		if (selectedTitle.equals("Grid table")) {
			copyMenuItem.setEnabled(false);
		}
		return popupMenu;
	}

	/*
	 * Finds the selected table IDs in the table.
	 */
	private void copyAction() {
		// Lấy hàng và cột đang chọn
		int selectedRow = table.getSelectedRow();
		int selectedColumn = table.getSelectedColumn();

		if (selectedRow != -1 && selectedColumn != -1) {
			// Lấy giá trị từ ô đang chọn
			Object value = table.getValueAt(selectedRow, selectedColumn);
			if (value != null) {
				// Sao chép giá trị vào clipboard
				StringSelection stringSelection = new StringSelection(value.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				Toast.show(this, Toast.Type.SUCCESS, "Copied to clipboard");
			} else {
				Toast.show(this, Toast.Type.INFO, "Cell is emty");
			}
		} else {
			Toast.show(this, Toast.Type.ERROR, "Please select a cell to copy");
		}
	}

	/**
	 * Creates a JButton with the specified text and action listener.
	 * 
	 * @param text           The text to display on the button.
	 * @param actionListener The action listener to handle button clicks.
	 * @return The created JButton.
	 */
	private JButton createButton(String text, ActionListener actionListener) {
		JButton button = new JButton(text);
		button.addActionListener(actionListener);
		return button;
	}

	/**
	 * Creates a search text field with debounce functionality.
	 * 
	 * @return The created JTextField for search.
	 */
	private JTextField createSearchTextField() {
		JTextField txtSearch = new JTextField();
		txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
		txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
				new FlatSVGIcon("raven/modal/demo/icons/search.svg", 0.4f));
		txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				handleSearchTextChange(txtSearch);
			}
		});
		return txtSearch;
	}

	/**
	 * Handles the search text change with debounce functionality.
	 * 
	 * @param txtSearch The JTextField containing the search text.
	 */
	private void handleSearchTextChange(JTextField txtSearch) {
		if (debounceTimer != null && debounceTimer.isRunning()) {
			debounceTimer.stop();
		}
		debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> loadData(txtSearch.getText()));
		debounceTimer.setRepeats(false);
		debounceTimer.start();
	}

	/**
	 * Refreshes the form by reloading the data.
	 */
	@Override
	public void formRefresh() {
		loadData("");
	}

	/**
	 * Loads data into the table based on the search term.
	 * 
	 * @param searchTerm The term to search for in the table.
	 */
	private volatile boolean isLoading = false;

	private void loadData(String searchTerm) {
		if (isLoading)
			return;
		isLoading = true;
		// Sử dụng ExecutorService để quản lý luồng
		ExecutorService executor = Executors.newSingleThreadExecutor(); // Chỉ cần 1 luồng cho công việc này
		executor.submit(() -> {
			try {
				panelCard.removeAll(); // Clear existing CardTable components
				List<ModelPromotion> promotions = fetchPromotions(searchTerm);
				if (promotions == null || promotions.isEmpty()) {
					Toast.show(this, Toast.Type.INFO, "No promotion in database or in search");
					return;
				}

				// Gọi các phương thức populate với danh sách tables
				populateCardTable(promotions);
				populateBasicTable(promotions);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// Đảm bảo giải phóng tài nguyên sau khi hoàn thành
				executor.shutdown(); // Tắt ExecutorService khi công việc hoàn thành
				System.gc(); // Gọi garbage collection nếu cần thiết
				isLoading = false;
			}
		});
	}

	private List<ModelPromotion> fetchPromotions(String searchTerm) throws IOException {
		if (searchTerm != null && !searchTerm.isEmpty()) {
			return controllerPromotion.searchPromotions(searchTerm);
		} else {
			return controllerPromotion.getAllPromotions();
		}
	}

	private void populateCardTable(List<ModelPromotion> promotions) {
		// Create a fixed thread pool with a number of threads
		ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust the number of threads as needed
		CountDownLatch latch = new CountDownLatch(promotions.size()); // Latch to wait for all tasks to complete

		// Create a list to store CardTables temporarily
		List<CardTable> cardTables = new ArrayList<>();

		// Sort the tables by ID and submit tasks to the executor
		promotions.parallelStream().forEach(modelTable -> {
			executor.submit(() -> {
				try {
					// Create a CardTable for each ModelTable
					CardTable cardTable = null;
					cardTable.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							showPopup(e);
						}

						private void showPopup(MouseEvent e) {
							if (e.getComponent() instanceof CardTable) {
								if (SwingUtilities.isLeftMouseButton(e)) {
									System.out.println("1: " + cardTable.isSelected());
									cardTable.setSelected(!cardTable.isSelected());
									System.out.println("2: " + cardTable.isSelected());
									if (cardTable.isSelected()) {
										cardTable.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green
									} else {
										cardTable.setBorder(BorderFactory.createEmptyBorder()); // Remove border when
									}
								} else if (e.isPopupTrigger()) {
									cardTable.setSelected(true);
									cardTable.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green border
									createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
								}
							}
						}
					});

					// Add the CardTable to the temporary list
					synchronized (cardTables) {
						cardTables.add(cardTable);
					}
				} finally {
					latch.countDown(); // Decrement the latch count
				}
			});
		});

		// Wait for all tasks to complete
		try {
			latch.await(); // Wait until all threads have finished
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
		} finally {
			executor.shutdown(); // Shutdown the executor
		}

		// Sort the cardTables list by ID (assuming the ID is accessible from
		// ModelTable)
		cardTables.sort(Comparator.comparingLong(cardTable -> cardTable.getModelTable().getId()));

		// Add sorted cardTables to the panelCard on the Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			cardTables.forEach(panelCard::add); // Add each cardTable to the panel
			panelCard.revalidate(); // Revalidate the panel to update the UI
			panelCard.repaint(); // Repaint the panel to reflect changes
		});
	}

	/**
	 * Populates the basic table with the fetched ModelTable objects.
	 * 
	 * @param tables The list of ModelTable objects to populate.
	 */
	private void populateBasicTable(List<ModelPromotion> promotions) {
		// Create a fixed thread pool with a number of threads
		ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust the number of threads as needed
		CountDownLatch latch = new CountDownLatch(promotions.size()); // Latch to wait for all tasks to complete

		// Clear the existing rows in the table model
		SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));

		// Create a list to store rows temporarily
		List<Object[]> rows = new ArrayList<>();

		// Sort the tables by ID and submit tasks to the executor
		promotions.parallelStream().forEach(promotion -> {
			executor.submit(() -> {
				try {
					// Add the row to the temporary list
					Object[] row = controllerPromotion.getBasicRow(promotion);
					synchronized (rows) {
						rows.add(row);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.show(this, Toast.Type.ERROR, "Failed to find menu relation with promotion");
				} finally {
					latch.countDown(); // Decrement the latch count
				}
			});
		});

		// Wait for all tasks to complete
		try {
			latch.await(); // Wait until all threads have finished
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
		} finally {
			executor.shutdown(); // Shutdown the executor
		}

		// Convert the list to an array for parallel sorting
		Object[][] rowsArray = rows.toArray(new Object[0][]);

		// Sort the rows using parallel sort (increasing ID)
		Arrays.parallelSort(rowsArray, (row1, row2) -> Long.compare((Long) row1[1], (Long) row2[1]));

		// Add sorted rows to the table model on the Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			for (Object[] row : rowsArray) {
				tableModel.addRow(row); // Add each row to the model
			}
		});
	}

	/**
	 * Displays a modal dialog for creating, editing, or deleting a table.
	 * 
	 * @param userAction The action to perform (create, edit, delete).
	 */
	private void showModal(String userAction) {
		switch (userAction) {
		case "details":
			showDetailsModal();
			break;
		case "create":
			showCreateModal();
			break;
		case "edit":
			showEditModal();
			break;
		case "delete":
			showDeleteModal();
			break;
		default:
			break;
		}
	}

	private void showDetailsModal() {
		long[] idHolder = { -1L };
		System.out.println("debug");
		if (selectedTitle.equals("Basic table")) {
			if (!validateSingleRowSelection("view details"))
				return;
			idHolder[0] = (long) table.getValueAt(table.getSelectedRow(), 1);
		} else if (selectedTitle.equals("Grid table")) {
			if (!validateSingleCardSelection(idHolder, "view details"))
				return;
		} else {
			idHolder[0] = (long) table.getValueAt(table.getSelectedRow(), 1);
		}

		InfoFormPromotion infoFormTable = createInfoFormPromotion(idHolder[0]);
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(infoFormTable, "Promotion details information",
				AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
				}), DefaultComponent.getInfoForm());
	}

	private void showCreateModal() {
		InputFormCreatePromotion inputFormCreatePromotion = new InputFormCreatePromotion(null);
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(inputFormCreatePromotion, "Create promotions",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleCreatePromotion(inputFormCreatePromotion);
					}
				}), DefaultComponent.getInputFormDoubleSize());
	}

	/**
	 * Creates an input form for displaying table details.
	 * 
	 * @param tableId The ID of the table to display.
	 * @return The created InfoFormTable, or null if an error occurs.
	 */
	private InfoFormPromotion createInfoFormPromotion(long promotionId) {
		try {
			return new InfoFormPromotion(promotionId);
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to find promotion to view details: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Handles the creation of a new table.
	 * 
	 * @param inputFormCreateTable The input form containing the table name.
	 */
	private void handleCreatePromotion(InputFormCreatePromotion inputFormCreatePromotion) {
		try {
			controllerPromotion.createPromotions(inputFormCreatePromotion.getPromotionData());
			Toast.show(this, Toast.Type.SUCCESS, "Create promotion successfully");
			loadData(""); // Reload table data after successful creation
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to create promotion: " + e.getMessage());
		}
	}

	/**
	 * Displays a modal dialog for editing an existing table.
	 */
	private void showEditModal() {
		long[] idHolder = { -1L };
		if (selectedTitle.equals("Basic table")) {
			if (!validateSingleRowSelection("edit"))
				return;
			idHolder[0] = (long) table.getValueAt(table.getSelectedRow(), 1);
		} else if (selectedTitle.equals("Grid table")) {
			if (!validateSingleCardSelection(idHolder, "edit"))
				return;
		}
		ModelPromotion promotion = null;
		try {
			promotion = controllerPromotion.getPromotionById(idHolder[0]);
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to find table to edit: " + e.getMessage());
		}
		if (!promotion.getStatus().equals(EnumStatusPromotion.AVAILABLE)) {
			Toast.show(this, Toast.Type.ERROR, "This promotion is not available to edit");
			return;
		}
		InputFormUpdatePromotion inputFormUpdatePromotion = createInputFormUpdatePromotion(idHolder[0]);
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(inputFormUpdatePromotion, "Update promotion",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleUpdatePromotion(inputFormUpdatePromotion);
					}
				}), DefaultComponent.getInputForm());
	}

	/**
	 * Validates if a single row is selected in the basic table.
	 * 
	 * @param a
	 * 
	 * @return True if a single row is selected, false otherwise.
	 */
	private boolean validateSingleRowSelection(String action) {
		if (table.getSelectedRowCount() > 1) {
			Toast.show(this, Toast.Type.ERROR, "Please select only one row to " + action);
			return false;
		}
		if (table.getSelectedRowCount() == 0) {
			Toast.show(this, Toast.Type.ERROR, "Please select a row to " + action);
			return false;
		}
		return true;
	}

	/**
	 * Validates if a single card is selected in the grid table.
	 * 
	 * @param idHolder The array to hold the selected table ID.
	 * @param a
	 * @return True if a single card is selected, false otherwise.
	 */
	private boolean validateSingleCardSelection(long[] idHolder, String action) {
		List<Long> selectedIds = findSelectedPromotionIds(panelCard);
		if (selectedIds.size() > 1) {
			Toast.show(this, Toast.Type.ERROR, "Please select only one row to " + action);
			return false;
		}
		if (selectedIds.isEmpty()) {
			Toast.show(this, Toast.Type.ERROR, "Please select a row to " + action);
			return false;
		}
		idHolder[0] = selectedIds.get(0);
		return true;
	}

	private ControllerMenu controllerMenu = new ControllerMenu();

	/**
	 * Creates an input form for updating a table.
	 * 
	 * @param tableId The ID of the table to update.
	 * @return The created InputFormUpdateTable, or null if an error occurs.
	 */
	private InputFormUpdatePromotion createInputFormUpdatePromotion(long promotionId) {
		ModelPromotion modelPromotion;
		try {
			modelPromotion = controllerPromotion.getPromotionById(promotionId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.show(this, Toast.Type.ERROR, "Failed to find table to edit: " + e.getMessage());
			return null;
		}
		ModelMenu modelMenu;
		try {
			modelMenu = controllerMenu.getMenuById(modelPromotion.getMenuId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.show(this, Toast.Type.ERROR, "Failed to find table to edit: " + e.getMessage());
			return null;
		}
		return new InputFormUpdatePromotion(modelPromotion, modelMenu);
	}

	/**
	 * Handles the update of an existing table.
	 * 
	 * @param inputFormUpdateTable The input form containing the updated table
	 *                             information.
	 * @param tableId              The ID of the table to update.
	 */
	private void handleUpdatePromotion(InputFormUpdatePromotion inputFormUpdatePromotion) {
		try {
			controllerPromotion.updatePromotion(inputFormUpdatePromotion.getPromotionData());
			Toast.show(this, Toast.Type.SUCCESS, "Update promotion successfully");
			loadData(""); // Reload table data after successful update
		} catch (IOException | BusinessException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to update promotion: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Displays a modal dialog for deleting selected tables.
	 */
	private void showDeleteModal() {
		List<Long> findSelectedTableIds = getSelectedTableIdsForDeletion();
		if (findSelectedTableIds.isEmpty() && (table.getSelectedRow() == -1)) {
			Toast.show(this, Toast.Type.ERROR, "You have to select at least one promotion to delete");
			return;
		}
		confirmDeletion(findSelectedTableIds);
	}

	/**
	 * Gets the IDs of the selected tables for deletion.
	 * 
	 * @return The list of selected table IDs.
	 */
	private List<Long> getSelectedTableIdsForDeletion() {
		if (selectedTitle.equals("Basic table")) {
			return findSelectedPromotionIds();
		} else if (selectedTitle.equals("Grid table")) {
			return findSelectedPromotionIds(panelCard);
		}
		return new ArrayList<>();
	}

	/**
	 * Confirms the deletion of the selected tables.
	 * 
	 * @param findSelectedTableIds The list of table IDs to delete.
	 */
	private void confirmDeletion(List<Long> findSelectedPromotionId) {
		if (findSelectedPromotionId.size() == 1) {
			confirmSingleDeletion(findSelectedPromotionId.get(0));
		} else {
			confirmMultipleDeletion(findSelectedPromotionId);
		}
	}

	/**
	 * Confirms the deletion of a single table.
	 * 
	 * @param tableId The ID of the table to delete.
	 */
	private void confirmSingleDeletion(Long promotionId) {
		ModalDialog.showModal(this,
				new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
						"Are you sure you want to delete this promotion: " + promotionId
								+ "? This action cannot be undone.",
						"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								deletePromotion(promotionId);
							}
						}),
				DefaultComponent.getChoiceModal());
	}

	/**
	 * Confirms the deletion of multiple tables.
	 * 
	 * @param findSelectedTableIds The list of table IDs to delete.
	 */
	private void confirmMultipleDeletion(List<Long> findSelectedPromotionIds) {
		ModalDialog.showModal(this,
				new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
						"Are you sure you want to delete these tables: " + findSelectedPromotionIds
								+ "? This action cannot be undone.",
						"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								deletePromotions(findSelectedPromotionIds);
							}
						}),
				DefaultComponent.getChoiceModal());
	}

	/**
	 * Deletes a single table by its ID.
	 * 
	 * @param tableId The ID of the table to delete.
	 */
	private void deletePromotion(long promotionId) {
		try {
			controllerPromotion.deletePromotion(promotionId);
			loadData(""); // Reload table data after successful deletion
			Toast.show(this, Toast.Type.SUCCESS, "Delete promotion successfully");
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to delete promotion: " + e.getMessage());
		}
	}

	/**
	 * Deletes multiple tables by their IDs.
	 * 
	 * @param tableIds The list of table IDs to delete.
	 */
	private void deletePromotions(List<Long> promotionIds) {
		try {
			controllerPromotion.deletePromotions(promotionIds);
			loadData(""); // Reload table data after successful deletion
			Toast.show(this, Toast.Type.SUCCESS, "Delete promotions successfully");
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to delete tables: " + e.getMessage());
		}
	}

	private static final int CHUNK_SIZE = 4; // Size of each chunk for processing

	/**
	 * Deletes selected tables from the basic table.
	 * 
	 * @return The list of table IDs to delete.
	 */
	private List<Long> findSelectedPromotionIds() {
		int rowCount = table.getRowCount();
		Set<Long> promotionIdsToDelete = Collections.synchronizedSet(new HashSet<>());
		List<List<Long>> chunks = createChunks(rowCount);
		ExecutorService executorService = Executors.newFixedThreadPool(4); // Use up to 4 threads
		List<Callable<Set<Long>>> tasks = new ArrayList<>();

		for (List<Long> chunk : chunks) {
			tasks.add(() -> new HashSet<>(processChunk(chunk))); // Đảm bảo chunk trả về dạng Set
		}

		try {
			List<Future<Set<Long>>> results = executorService.invokeAll(tasks);
			for (Future<Set<Long>> result : results) {
				promotionIdsToDelete.addAll(result.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}
		return new ArrayList<>(promotionIdsToDelete);
	}

	/**
	 * Creates chunks of table rows for processing.
	 * 
	 * @param rowCount The total number of rows in the table.
	 * @return The list of chunks containing table IDs.
	 */
	private List<List<Long>> createChunks(int rowCount) {
		List<List<Long>> chunks = new ArrayList<>();
		List<Long> currentChunk = new ArrayList<>();

		for (int i = 0; i < rowCount; i++) {
			Long tableId = (Long) table.getValueAt(i, 1); // Column 1 contains the table ID
			currentChunk.add(tableId);

			if (currentChunk.size() == CHUNK_SIZE) {
				chunks.add(new ArrayList<>(currentChunk));
				currentChunk.clear();
			}
		}

		if (!currentChunk.isEmpty()) {
			chunks.add(currentChunk);
		}

		return chunks;
	}

	/**
	 * Processes a chunk of table rows and returns the list of table IDs to delete.
	 * 
	 * @param chunk The list of table IDs in the chunk.
	 * @return The list of table IDs to delete.
	 */
	private List<Long> processChunk(List<Long> chunk) {
		List<Long> promotionIdsToDelete = new ArrayList<>();

		for (Long promotionId : chunk) {
			int rowIndex = findRowIndexById(promotionId);
			Boolean isChecked = (Boolean) table.getValueAt(rowIndex, 0); // Column 0 contains the boolean value
			if (isChecked != null && isChecked) {
				promotionIdsToDelete.add(promotionId);
			}
		}

		return promotionIdsToDelete;
	}

	/**
	 * Finds the row index of a table by its ID.
	 * 
	 * @param tableId The ID of the table to find.
	 * @return The row index of the table.
	 */
	private int findRowIndexById(Long tableId) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 1).equals(tableId)) {
				return i;
			}
		}
		return -1; // Not found
	}

	/**
	 * Finds the IDs of selected tables in the grid table.
	 * 
	 * @param panelCard The JPanel containing the CardTable components.
	 * @return The list of selected table IDs.
	 */
	private List<Long> findSelectedPromotionIds(JPanel panelCard) {
		List<Long> selectedTableIds = new ArrayList<>();
		Component[] components = panelCard.getComponents();
		List<List<Component>> chunks = createComponentChunks(components);
		ExecutorService executorService = Executors.newFixedThreadPool(4); // Use up to 4 threads
		List<Callable<List<Long>>> tasks = new ArrayList<>();

		for (List<Component> chunk : chunks) {
			tasks.add(() -> processComponentChunk(chunk));
		}

		try {
			List<Future<List<Long>>> results = executorService.invokeAll(tasks);
			for (Future<List<Long>> result : results) {
				selectedTableIds.addAll(result.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}

		return selectedTableIds;
	}

	/**
	 * Creates chunks of components for processing.
	 * 
	 * @param components The array of components to chunk.
	 * @return The list of chunks containing components.
	 */
	private List<List<Component>> createComponentChunks(Component[] components) {
		List<List<Component>> chunks = new ArrayList<>();
		List<Component> currentChunk = new ArrayList<>();

		for (Component component : components) {
			currentChunk.add(component);

			if (currentChunk.size() == CHUNK_SIZE) {
				chunks.add(new ArrayList<>(currentChunk));
				currentChunk.clear();
			}
		}

		if (!currentChunk.isEmpty()) {
			chunks.add(currentChunk);
		}

		return chunks;
	}

	/**
	 * Processes a chunk of components and returns the list of selected table IDs.
	 * 
	 * @param chunk The list of components in the chunk.
	 * @return The list of selected table IDs.
	 */
	private List<Long> processComponentChunk(List<Component> chunk) {
		List<Long> selectedIds = new ArrayList<>();

		for (Component component : chunk) {
			if (component instanceof CardPromotion) {
				CardPromotion cardPromotion = (CardPromotion) component;
				if (cardPromotion.isSelected()) {
					selectedIds.add(cardPromotion.getModelPromotion().getId());
				}
			}
		}

		return selectedIds;
	}

	/**
	 * Creates the table model for managing rows and columns.
	 * 
	 * @return The created DefaultTableModel.
	 */
	private DefaultTableModel createTableModel() {
		return new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0; // Allow cell editing only for checkbox column
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return Boolean.class; // Boolean for checkbox
				if (columnIndex == 2 || columnIndex == 6) {
					return ThumbnailCell.class; // Double for price columns
				}
				return super.getColumnClass(columnIndex);
			}
		};
	}
}