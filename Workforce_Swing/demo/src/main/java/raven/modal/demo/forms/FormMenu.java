package raven.modal.demo.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;
import raven.modal.demo.controllers.ControllerMenu; // Updated to ControllerMenu
import raven.modal.demo.forms.cards.CardMenu;
import raven.modal.demo.forms.info.InfoFormMenu; // Updated to InfoFormMenu
import raven.modal.demo.forms.input.InputFormCreateMenu; // Updated to InputFormCreateMenu
import raven.modal.demo.forms.input.InputFormUpdateMenu; // Updated to InputFormUpdateMenu
import raven.modal.demo.layout.ResponsiveLayout;
import raven.modal.demo.models.ModelProfile;
import raven.modal.demo.models.EnumMenuCategory;
import raven.modal.demo.models.EnumMenuStatus;
import raven.modal.demo.models.ModelMenu; // Updated to ModelMenu
import raven.modal.demo.services.ServiceMenu; // Updated to ServiceMenu
import raven.modal.demo.services.impls.ServiceImplMenu; // Updated to ServiceImplMenu
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

@SystemForm(name = "Menu", description = "Menu is a user interface component", tags = { "list" })
public class FormMenu extends Form {
	private ControllerMenu controllerMenu = new ControllerMenu(); // Controller for handling menu operations
	private ServiceMenu menuService = new ServiceImplMenu(); // Service layer for menu-related operations
	private final Object columns[] = new Object[] { "SELECT", "#", "THUMBNAIL", "CATEGORY", "ORIGINAL PRICE",
			"SALE PRICE", "CREATED TIME", "UPDATED TIME" };
	private DefaultTableModel tableModel = createTableModel(); // Table model for managing rows and columns
	private JTable table = new JTable(tableModel);
	private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT,
			new Dimension(-1, -1), 10, 10);
	private JPanel panelCard = new JPanel(responsiveLayout);
	private static final int DEBOUNCE_DELAY = 1000; // Debounce delay in milliseconds
	private Timer debounceTimer;
	private String selectedTitle = "Basic table"; // Default selected title";

	public FormMenu() {
		init();
	}

	/**
	 * Initializes the form layout and components.
	 */
	private void init() {
		setLayout(new MigLayout("fillx,wrap", "[fill]", "[][fill,grow]"));
		add(createInfoPanel("Menu Management",
				"This is a user interface component that displays a collection of menus in a structured, tabular format. It allows users to view, sort, and manage data or other resources.",
				1));
		add(createTabPanel(), "gapx 7 7");
	}

	/**
	 * Creates an information panel with a title and description.
	 * 
	 * @param title       The title of the panel.
	 * @param description The description of the panel.
	 * @param level       The level of the title font size.
	 * @return The created JPanel.
	 */
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

	/**
	 * Creates the tab panel containing different menu views.
	 * 
	 * @return The created JTabbedPane.
	 */
	private Component createTabPanel() {
		JTabbedPane tabb = new JTabbedPane();
		tabb.putClientProperty(FlatClientProperties.STYLE, "" + "tabType:card");
		tabb.addTab("Basic table", createBorder(createBasicMenu()));
		tabb.addTab("Grid table", createBorder(createGridMenu()));
		tabb.addChangeListener(e -> {
			loadData("");
			selectedTitle = tabb.getTitleAt(tabb.getSelectedIndex());
		});
		return tabb;
	}

	/**
	 * Creates a bordered panel for the given component.
	 * 
	 * @param component The component to be bordered.
	 * @return The created JPanel with border.
	 */
	private Component createBorder(Component component) {
		JPanel panel = new JPanel(new MigLayout("fill,insets 7 0 7 0", "[fill]", "[fill]"));
		panel.add(component);
		return panel;
	}

	/**
	 * Creates the basic menu view.
	 * 
	 * @return The created JPanel containing the basic menu.
	 */
	private Component createBasicMenu() {
		JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
		configureTableProperties();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		styleTableWithFlatLaf(scrollPane);
		panel.add(createTableTitle("Basic Menu"), "gapx 20");
		panel.add(createHeaderActionPanel());
		panel.add(scrollPane);
		loadData("");
		return panel;
	}

	/**
	 * Configures the properties of the table.
	 */
	private void configureTableProperties() {
		table.setModel(tableModel);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setShowGrid(false); // Tắt các đường lưới
		// Tắt đường viền trong bảng
		table.setIntercellSpacing(new Dimension(0, 0)); // Không có khoảng cách giữa các ô
		table.setRowHeight(50);
		table.setSelectionBackground(new Color(220, 230, 241));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);// original price
		table.getColumnModel().getColumn(5).setPreferredWidth(150);// sale price
		table.getColumnModel().getColumn(6).setPreferredWidth(250);
		table.getColumnModel().getColumn(7).setPreferredWidth(250);
		table.setDefaultRenderer(ThumbnailCell.class, new TableThumbnailRenderer(table));
		table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
		table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
			@Override
			protected int getAlignment(int column) {
				int trailing[] = { 5, 6, 7, 8 };
				int center[] = {2 };
				// Kiểm tra nếu column nằm trong trailing
				if (Arrays.stream(trailing).anyMatch(value -> value == column)) {
					return SwingConstants.TRAILING; // Hoặc giá trị mong muốn
				}
				if (Arrays.stream(center).anyMatch(value -> value == column)) {
					return SwingConstants.CENTER;
				}
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
						// cell selection
						table.repaint();
					}
					table.setValueAt(true, row, 0);
					createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * Styles the table with FlatLaf properties.
	 * 
	 * @param scrollPane The JScrollPane containing the table.
	 */
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

	/**
	 * Creates a title label for the menu.
	 * 
	 * @param title The title text.
	 * @return The created JLabel.
	 */
	private JLabel createTableTitle(String title) {
		JLabel titleLabel = new JLabel(title);
		titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
		return titleLabel;
	}

	/**
	 * Creates the grid menu view.
	 * 
	 * @return The created JPanel containing the grid menu.
	 */
	private Component createGridMenu() {
		JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
		configurePanelCardStyle();
		JScrollPane scrollPane = createScrollPaneForPanelCard();
		panel.add(createTableTitle("Grid Menu"), "gapx 20");
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
		JMenuItem deleteMenuItem = new JMenuItem(((findSelectedMenuIds().size() == 1)|| (findSelectedMenuIds(panelCard).size() == 1)) ? "Delete" : "Delete many");
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
	// Biến trạng thái để theo dõi hàm đang chạy
	private volatile boolean isLoading = false;

	private void loadData(String searchTerm) {
	    if (isLoading) {
	        return;
	    }

	    // Đặt trạng thái isLoading thành true
	    isLoading = true;

	    // Sử dụng ExecutorService để quản lý luồng
	    ExecutorService executor = Executors.newSingleThreadExecutor(); // Chỉ cần 1 luồng cho công việc này
	    executor.submit(() -> {
	        try {
	            panelCard.removeAll(); // Clear existing CardMenu components
	            List<ModelMenu> menus = fetchMenus(searchTerm);
	            if (menus == null || menus.isEmpty()) {
	                Toast.show(this, Toast.Type.INFO, "No menu in database or in search");
	                return;
	            }

	            // Gọi các phương thức populate với danh sách menu
	            populateCardMenu(menus);
	            populateBasicMenu(menus);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            // Đảm bảo giải phóng tài nguyên sau khi hoàn thành
	            executor.shutdown(); // Tắt ExecutorService khi công việc hoàn thành
	            System.gc(); // Gọi garbage collection nếu cần thiết

	            // Đặt trạng thái isLoading về false sau khi hoàn tất
	            isLoading = false;
	        }
	    });
	}


	/**
	 * Fetches the list of menus based on the search term.
	 * 
	 * @param searchTerm The term to search for in the menu.
	 * @return The list of ModelMenu objects.
	 * @throws IOException If an I/O error occurs.
	 */
	private List<ModelMenu> fetchMenus(String searchTerm) throws IOException {
		if (searchTerm != null && !searchTerm.isEmpty()) {
			return controllerMenu.searchMenus(searchTerm);
		} else {
			return controllerMenu.getAllMenus();
		}
	}

	/**
	 * Populates the card menu with the fetched ModelMenu objects.
	 * 
	 * @param menus The list of ModelMenu objects to populate.
	 */
	private void populateCardMenu(List<ModelMenu> menus) {
		// Create a fixed thread pool with a number of threads
		ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust the number of threads as needed
		CountDownLatch latch = new CountDownLatch(menus.size()); // Latch to wait for all tasks to complete

		// Sort the menus by ID and submit tasks to the executor
		menus.parallelStream().forEach(modelMenu -> {
			executor.submit(() -> {
				try {
					// Create a CardMenu for each ModelMenu
					CardMenu cardMenu = new CardMenu(modelMenu, null);
					cardMenu.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							showPopup(e);
						}

						private void showPopup(MouseEvent e) {
							if (e.getComponent() instanceof CardMenu) {
								if (SwingUtilities.isLeftMouseButton(e)) {
									cardMenu.setSelected(!cardMenu.isSelected());
									if (cardMenu.isSelected()) {
										cardMenu.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green
																											// border
									} else {
										cardMenu.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not
									}
								} else if (e.isPopupTrigger()) {
									cardMenu.setSelected(true);
									cardMenu.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Green border
									createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
								}
							}
						}
					});
					// Add the cardMenu to the panelCard on the Event Dispatch Thread
					SwingUtilities.invokeLater(() -> panelCard.add(cardMenu));
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
			panelCard.revalidate(); // Revalidate the panel to update the UI
			panelCard.repaint(); // Repaint the panel to reflect changes
		}
	}

	/**
	 * Populates the basic menu with the fetched ModelMenu objects using
	 * multithreading.
	 * 
	 * @param menus The list of ModelMenu objects to populate.
	 */
	private static int count = 1;
	private void populateBasicMenu(List<ModelMenu> menus) {
		//Delete all row in table
		((DefaultTableModel)table.getModel()).setRowCount(0);
		// Create a fixed thread pool with a number of threads
		ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust the number of threads as needed
		CountDownLatch latch = new CountDownLatch(menus.size()); // Latch to wait for all tasks to complete

		// Clear the existing rows in the table model
		// Sort the menus by ID and submit tasks to the executor
		menus.parallelStream().sorted(Comparator.comparing(ModelMenu::getId)).forEach(modelMenu -> {
			executor.submit(() -> {
				try {
					// Add the row to the table model on the Event Dispatch Thread
					SwingUtilities.invokeLater(() -> tableModel.addRow(modelMenu.toTableRowBasic()));
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
	}

	/**
	 * Displays a modal dialog for creating, editing, or deleting a menu.
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

	/*
	 * Displays a modal dialog for displaying menu details.
	 */
	private void showDetailsModal() {
		String[] idHolder = { "" };
		if (selectedTitle.equals("Basic table")) {
			if (!validateSingleRowSelection("view details"))
				return;
			idHolder[0] = table.getValueAt(table.getSelectedRow(), 1).toString();
		} else if (selectedTitle.equals("Grid table")) {
			if (!validateSingleCardSelection(idHolder, "view details"))
				return;
		} else {
			idHolder[0] = table.getValueAt(table.getSelectedRow(), 1).toString();
		}

		InfoFormMenu infoFormMenu = createInfoFormMenu(idHolder[0]);
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(infoFormMenu, "Menu details information",
				AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
				}), DefaultComponent.getInfoForm());
	}

	/**
	 * Displays a modal dialog for creating a new menu.
	 */
	private void showCreateModal() {
	    InputFormCreateMenu inputFormCreateMenu = new InputFormCreateMenu();
	    // Show the modal dialog and handle the result directly in the event handler
	    ModalDialog.showModal(this, new AdaptSimpleModalBorder(inputFormCreateMenu, "Create menu",
	            AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
	                if (action == AdaptSimpleModalBorder.YES_OPTION) {
	                    handleCreateMenu(inputFormCreateMenu);
	                }
	            }), DefaultComponent.getInputForm());

	    // No need to wait here, the UI will not be blocked
	}


	/**
	 * Handles the creation of a new menu.
	 * 
	 * @param inputFormCreateMenu The input form containing the menu name.
	 */
	private void handleCreateMenu(InputFormCreateMenu inputFormCreateMenu) {
		Object[] menuData = inputFormCreateMenu.getMenuData();
		  try { 
			  controllerMenu.createMenu(menuData); 
			  Toast.show(this, Toast.Type.SUCCESS,
		  "Create menu successfully"); loadData(""); // Reload menu data after successful creation 
		  } catch (IOException e) { Toast.show(this,
		  Toast.Type.ERROR, "Failed to create menu: " + e.getMessage()); }
		 
	}

	/**
	 * Displays a modal dialog for editing an existing menu.
	 */
	private void showEditModal() {
	    String[] idHolder = { "" };
	    if (selectedTitle.equals("Basic table")) {
	        if (!validateSingleRowSelection("edit"))
	            return;
	        idHolder[0] = table.getValueAt(table.getSelectedRow(), 1).toString();
	    } else if (selectedTitle.equals("Grid table")) {
	        if (!validateSingleCardSelection(idHolder, "edit"))
	            return;
	    } else {
	        idHolder[0] = table.getValueAt(table.getSelectedRow(), 1).toString();
	    }

	    // Cannot edit deleted menu
	    ModelMenu menu = null;
	    try {
	        menu = controllerMenu.getMenuById(idHolder[0]);
	    } catch (IOException e) {
	        Toast.show(this, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
	    }

	    if ( menu.getStatus().equals(EnumMenuStatus.DELETED)) {
	        Toast.show(this, Toast.Type.ERROR, "Cannot edit deleted menu");
	        return;
	    }

	    InputFormUpdateMenu inputFormUpdateMenu = createInputFormUpdateMenu(menu);

	    // Show the modal dialog and handle the result directly in the event
	    ModalDialog.showModal(this, new AdaptSimpleModalBorder(inputFormUpdateMenu, "Update menu",
	            AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
	                if (action == AdaptSimpleModalBorder.YES_OPTION) {
	                    handleUpdateMenu(inputFormUpdateMenu);
	                }
	            }), DefaultComponent.getInputForm());

	    // No need to wait here, the UI will not be blocked
	}


	/**
	 * Validates if a single row is selected in the basic menu.
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
	 * Validates if a single card is selected in the grid menu.
	 * 
	 * @param idHolder The array to hold the selected menu ID.
	 * @return True if a single card is selected, false otherwise.
	 */
	private boolean validateSingleCardSelection(String[] idHolder, String action) {
		List<String> selectedIds = findSelectedMenuIds(panelCard);
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

	/**
	 * Creates an input form for updating a menu.
	 * 
	 * @param menu The ID of the menu to update.
	 * @return The created InputFormUpdateMenu, or null if an error occurs.
	 */
	private InputFormUpdateMenu createInputFormUpdateMenu(ModelMenu menu) {
		try {
			return new InputFormUpdateMenu(menu);
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Creates an input form for displaying menu details.
	 * 
	 * @param menuId The ID of the menu to display.
	 * @return The created InfoFormMenu, or null if an error occurs.
	 */
	private InfoFormMenu createInfoFormMenu(String menuId) {
		try {
			return new InfoFormMenu(menuId);
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to find menu to view details: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Handles the update of an existing menu.
	 * 
	 * @param inputFormUpdateMenu The input form containing the updated menu
	 *                            information.
	 * @param menuId              The ID of the menu to update.
	 */
	private void handleUpdateMenu(InputFormUpdateMenu inputFormUpdateMenu) {
		Object[] menuData = inputFormUpdateMenu.getMenuData();//inputFormUpdateMenu.getMenuData();
		try {
			controllerMenu.updateMenu(menuData);
			Toast.show(this, Toast.Type.SUCCESS, "Update menu successfully");
			loadData(""); // Reload menu data after successful update
		} catch (IOException | BusinessException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to update menu: " + e.getMessage());
		}
	}

	/**
	 * Displays a modal dialog for deleting selected menus.
	 */
	private void showDeleteModal() {
		List<String> findSelectedMenuIds = getSelectedMenuIdsForDeletion();
		if (findSelectedMenuIds.isEmpty()) {
			Toast.show(this, Toast.Type.ERROR, "You have to select at least one menu to delete");
			return;
		}
		confirmDeletion(findSelectedMenuIds);
	}

	/**
	 * Gets the IDs of the selected menus for deletion.
	 * 
	 * @return The list of selected menu IDs.
	 */
	private List<String> getSelectedMenuIdsForDeletion() {
		if (selectedTitle.equals("Basic table")) {
			return findSelectedMenuIds();
		} else if (selectedTitle.equals("Grid table")) {
			return findSelectedMenuIds(panelCard);
		}
		return new ArrayList<>();
	}

	/**
	 * Confirms the deletion of the selected menus.
	 * 
	 * @param findSelectedMenuIds The list of menu IDs to delete.
	 */
	private void confirmDeletion(List<String> findSelectedMenuIds) {
		if (findSelectedMenuIds.size() == 1) {
			confirmSingleDeletion(findSelectedMenuIds.get(0));
		} else {
			confirmMultipleDeletion(findSelectedMenuIds);
		}
	}

	/**
	 * Confirms the deletion of a single menu.
	 * 
	 * @param menuId The ID of the menu to delete.
	 */
	private void confirmSingleDeletion(String menuId) {
		ModalDialog.showModal(this,
				new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
						"Are you sure you want to delete this menu: " + menuId + "? This action cannot be undone.",
						"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								deleteMenu(menuId);
							}
						}),
				DefaultComponent.getChoiceModal());
	}

	/**
	 * Confirms the deletion of multiple menus.
	 * 
	 * @param findSelectedMenuIds The list of menu IDs to delete.
	 */
	private void confirmMultipleDeletion(List<String> findSelectedMenuIds) {
		ModalDialog.showModal(this,
				new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
						"Are you sure you want to delete these menus:" + findSelectedMenuIds
								+ "? This action cannot be undone.",
						"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								deleteMenus(findSelectedMenuIds);
							}
						}),
				DefaultComponent.getChoiceModal());
	}

	/**
	 * Deletes a single menu by its ID.
	 * 
	 * @param menuId The ID of the menu to delete.
	 */
	private void deleteMenu(String menuId) {
		try {
			controllerMenu.deleteMenu(menuId);
			loadData(""); // Reload menu data after successful deletion
			Toast.show(this, Toast.Type.SUCCESS, "Delete menu successfully");
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to delete menu: " + e.getMessage());
		}
	}

	/**
	 * Deletes multiple menus by their IDs.
	 * 
	 * @param findSelectedMenuIds The list of menu IDs to delete.
	 */
	private void deleteMenus(List<String> findSelectedMenuIds) {
		try {
			controllerMenu.deleteMenus(findSelectedMenuIds);
			loadData(""); // Reload menu data after successful deletion
			Toast.show(this, Toast.Type.SUCCESS, "Delete menus successfully");
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to delete menus: " + e.getMessage());
		}
	}

	private static final int CHUNK_SIZE = 4; // Size of each chunk for processing

	/**
	 * Deletes selected menus from the basic menu.
	 * 
	 * @return The list of menu IDs to delete.
	 */
	private List<String> findSelectedMenuIds() {
		int rowCount = table.getRowCount();
		List<String> menuIdsToDelete = new ArrayList<>();
		List<List<String>> chunks = createChunks(rowCount);
		ExecutorService executorService = Executors.newFixedThreadPool(4); // Use up to 4 threads
		List<Callable<List<String>>> tasks = new ArrayList<>();

		for (List<String> chunk : chunks) {
			tasks.add(() -> processChunk(chunk));
		}

		try {
			List<Future<List<String>>> results = executorService.invokeAll(tasks);
			for (Future<List<String>> result : results) {
				menuIdsToDelete.addAll(result.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}
		return menuIdsToDelete;
	}

	/**
	 * Creates chunks of menu rows for processing.
	 * 
	 * @param rowCount The total number of rows in the menu.
	 * @return The list of chunks containing menu IDs.
	 */
	private List<List<String>> createChunks(int rowCount) {
		List<List<String>> chunks = new ArrayList<>();
		List<String> currentChunk = new ArrayList<>();

		for (int i = 0; i < rowCount; i++) {
			String menuId = table.getValueAt(i, 1).toString(); // Column 1 contains the menu ID
			currentChunk.add(menuId);

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
	 * Processes a chunk of menu rows and returns the list of menu IDs to delete.
	 * 
	 * @param chunk The list of menu IDs in the chunk.
	 * @return The list of menu IDs to delete.
	 */
	private List<String> processChunk(List<String> chunk) {
		List<String> menuIdsToDelete = new ArrayList<>();

		for (String menuId : chunk) {
			int rowIndex = findRowIndexById(menuId);
			Boolean isChecked = (Boolean) table.getValueAt(rowIndex, 0); // Column 0 contains the boolean value
			if (isChecked != null && isChecked) {
				menuIdsToDelete.add(menuId);
			}
		}

		return menuIdsToDelete;
	}

	/**
	 * Finds the row index of a menu by its ID.
	 * 
	 * @param menuId The ID of the menu to find.
	 * @return The row index of the menu.
	 */
	private int findRowIndexById(String menuId) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i, 1).equals(menuId)) {
				return i;
			}
		}
		return -1; // Not found
	}

	/**
	 * Finds the IDs of selected menus in the grid menu.
	 * 
	 * @param panelCard The JPanel containing the CardMenu components.
	 * @return The list of selected menu IDs. ```java
	 */
	private List<String> findSelectedMenuIds(JPanel panelCard) {
		List<String> selectedMenuIds = new ArrayList<>();
		Component[] components = panelCard.getComponents();
		List<List<Component>> chunks = createComponentChunks(components);
		ExecutorService executorService = Executors.newFixedThreadPool(4); // Use up to 4 threads
		List<Callable<List<String>>> tasks = new ArrayList<>();

		for (List<Component> chunk : chunks) {
			tasks.add(() -> processComponentChunk(chunk));
		}

		try {
			List<Future<List<String>>> results = executorService.invokeAll(tasks);
			for (Future<List<String>> result : results) {
				selectedMenuIds.addAll(result.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}

		return selectedMenuIds;
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
	 * Processes a chunk of components and returns the list of selected menu IDs.
	 * 
	 * @param chunk The list of components in the chunk.
	 * @return The list of selected menu IDs.
	 */
	private List<String> processComponentChunk(List<Component> chunk) {
		List<String> selectedIds = new ArrayList<>();

		for (Component component : chunk) {
			if (component instanceof CardMenu) {
				CardMenu cardMenu = (CardMenu) component;
				if (cardMenu.isSelected()) {
					selectedIds.add(cardMenu.getModelMenu().getId());
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
				if (columnIndex == 2) {
					return ThumbnailCell.class; // Double for price columns
				}
				return super.getColumnClass(columnIndex);
			}
		};
	}
}