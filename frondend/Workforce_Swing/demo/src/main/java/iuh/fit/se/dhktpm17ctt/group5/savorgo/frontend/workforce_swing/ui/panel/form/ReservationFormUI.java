package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JSpinner;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.TableController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.MenuInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.TableInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.SystemForm;

import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.awt.Color;
import java.awt.Component;

@SystemForm(name = "Reservation", description = "Reservation is a user interface component to make reservation", tags = {
		"reservation" })

public class ReservationFormUI extends Form {

	private static final long serialVersionUID = 1L;
	private JTextField txtPhoneNumber;
	private JTextField txtCustomerName;
	private JTextField txtStaffName;
	private JTextField txtTotalAmountAfterDiscount;
	private JTextField txtTotalAmountBeforeDiscount;
	private JTextField txtChange;
	private JTextField txtSearchReservation;
	private JScrollPane panelList;
	private JPanel panelPayment;
	private JSpinner spinnerAmountGiven;
	private JTextField txtSearchCustomer;
	private JTextField txtSearchStaff;
	private JTextField txtCustomerId;
	private JTextField txtMembership;
	private JTextField txtGender;
	private JTextField txtStaffId;
	private JTextField txtDuration;
	private JTable tblRoom;
	private JTable tblService;
	private JPanel panelReserveAndPayment;
	private JLabel lblDuration;
	private JPanel cardPanel;

	public ReservationFormUI() {
		init();
	}

	private void init() {
		setLayout(new MigLayout("", "[40%:40%:40%,grow][grow][55%:55%:55%,grow]",
				"[109.00][370.00,grow][30px:40px:40px]"));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				SwingUtilities.invokeLater(() -> {
					revalidate();
					repaint();
				});
			}
		});

		cardPanel = new JPanel();
		cardPanel.setLayout(new MigLayout("wrap 1", "[grow]", "")); // Allow components to grow horizontally

		JPanel panelControl = new JPanel();
		add(panelControl, "cell 0 0,grow");
		panelControl.setLayout(new MigLayout("", "[][][][grow]", "[30px:40px:40px][][]"));

		JButton btnShowReservationList = new JButton("Show Reservation List");
		btnShowReservationList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelControl.add(btnShowReservationList, "cell 0 0,growx");

		JButton btnShowTables = new JButton("Show Tables");
		btnShowTables.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelControl.add(btnShowTables, "cell 1 0,growx");
		btnShowTables.addActionListener(e -> buttonAction("tables"));
		JButton btnShowMenus = new JButton("Show Menus");
		btnShowMenus.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelControl.add(btnShowMenus, "flowx,cell 2 0,growx");
		btnShowMenus.addActionListener(e -> buttonAction("menus"));

		txtSearchReservation = new JTextField();
		txtSearchReservation.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelControl.add(txtSearchReservation, "cell 0 1 4 1,growx");

		JLabel lblSelectButton = new JLabel("Please select a button above to show any list!");
		lblSelectButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
		panelControl.add(lblSelectButton, "cell 0 2 4 1,growx");

		panelList = new JScrollPane();
		panelList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar
		panelList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Never show horizontal
																						// scrollbar

		// Add the scrollPane to the main panel
		add(panelList, "cell 0 1,grow"); // Replace panelList with scrollPane

		panelPayment = new JPanel();
		add(panelPayment, "cell 2 0 1 2,grow");
		panelPayment.setLayout(new MigLayout("", "[100px:100px][80px:80px:80px][grow]",
				"[][][:30px:40px,grow][30px:40px:40px,fill][30px:40px:40px][][:30px:40px,grow][30px:40px:40px][:30px:40px,grow][30px:100px,grow][:30px:40px,grow][30px:100px,grow][30px:40px:40px][30px:40px:40px][grow][30px:40px:40px][grow]"));

		JLabel lblCustomerInfo = new JLabel("Customer Information");
		lblCustomerInfo.setFont(new Font("Times New Roman", Font.BOLD, 20));
		panelPayment.add(lblCustomerInfo, "cell 0 1 3 1,growx");

		txtSearchCustomer = new JTextField();
		txtSearchCustomer.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelPayment.add(txtSearchCustomer, "cell 0 2 3 1,growx");
		txtSearchCustomer.setColumns(10);

		txtCustomerId = new JTextField();
		txtCustomerId.setForeground(Color.WHITE);
		txtCustomerId.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtCustomerId.setColumns(10);
		panelPayment.add(txtCustomerId, "cell 0 3,growx");

		txtGender = new JTextField();
		txtGender.setForeground(Color.WHITE);
		txtGender.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtGender.setColumns(10);
		panelPayment.add(txtGender, "cell 1 3,grow");

		txtPhoneNumber = new JTextField();
		txtPhoneNumber.setForeground(Color.WHITE);
		txtPhoneNumber.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelPayment.add(txtPhoneNumber, "cell 2 3,grow");
		txtPhoneNumber.setColumns(10);

		txtMembership = new JTextField();
		txtMembership.setForeground(Color.WHITE);
		txtMembership.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtMembership.setColumns(10);
		panelPayment.add(txtMembership, "cell 0 4 2 1,grow");

		txtCustomerName = new JTextField();
		txtCustomerName.setForeground(Color.WHITE);
		txtCustomerName.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtCustomerName.setColumns(10);
		panelPayment.add(txtCustomerName, "flowx,cell 2 4,grow");

		JLabel lblRoomList = new JLabel("Selected Room List");
		lblRoomList.setFont(new Font("Times New Roman", Font.BOLD, 20));
		panelPayment.add(lblRoomList, "cell 0 5 2 1");

		JScrollPane spRoomList = new JScrollPane();
		tblRoom = new JTable();
		tblRoom.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Room ID", "Room Name", "Room Price",
				"Start", "End", "Hours", "Total Amount", "Function" }));
		spRoomList.setViewportView(tblRoom);
		panelPayment.add(spRoomList, "cell 0 6 3 1,grow");

		JLabel lblServiceList = new JLabel("Selected Service List");
		lblServiceList.setFont(new Font("Times New Roman", Font.BOLD, 20));
		panelPayment.add(lblServiceList, "cell 0 7");

		JScrollPane spServiceList = new JScrollPane();
		tblService = new JTable();
		tblService.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Service ID", "Service Name",
				"Service Price", "Selected Quantity", "Total Amount", "Function" }));
		spServiceList.setViewportView(tblService);
		panelPayment.add(spServiceList, "cell 0 8 3 1,grow");

		txtTotalAmountBeforeDiscount = new JTextField();
		txtTotalAmountBeforeDiscount.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtTotalAmountBeforeDiscount.setColumns(10);
		panelPayment.add(txtTotalAmountBeforeDiscount, "cell 0 9 2 1,grow");

		txtTotalAmountAfterDiscount = new JTextField();
		txtTotalAmountAfterDiscount.setHorizontalAlignment(JTextField.TRAILING);
		txtTotalAmountAfterDiscount.setForeground(Color.RED);
		txtTotalAmountAfterDiscount.setFont(new Font("Times New Roman", Font.BOLD, 15));
		txtTotalAmountAfterDiscount.setColumns(10);
		panelPayment.add(txtTotalAmountAfterDiscount, "cell 2 9,grow");

		JButton btnReserveOrPay = new JButton("Reserve");
		panelPayment.add(btnReserveOrPay, "cell 0 10 3 1,grow");

		panelReserveAndPayment = new JPanel();
		panelPayment.add(panelReserveAndPayment, "cell 0 11 3 1,grow");
		panelReserveAndPayment
				.setLayout(new MigLayout("", "[106px][grow]", "[30px:40px:40px][30px:40px:40px][30px:40px:40px]"));

		JLabel lblAmountGiven = new JLabel("Amount Given by Customer");
		lblAmountGiven.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelReserveAndPayment.add(lblAmountGiven, "cell 0 0,alignx trailing");

		spinnerAmountGiven = new JSpinner();
		spinnerAmountGiven.setForeground(Color.WHITE);
		spinnerAmountGiven.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelReserveAndPayment.add(spinnerAmountGiven, "cell 1 0,grow");

		JLabel lblChange = new JLabel("Change");
		lblChange.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelReserveAndPayment.add(lblChange, "cell 0 1,alignx trailing");

		txtChange = new JTextField();
		txtChange.setForeground(Color.WHITE);
		txtChange.setHorizontalAlignment(JTextField.TRAILING);
		txtChange.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtChange.setColumns(10);
		panelReserveAndPayment.add(txtChange, "cell 1 1,grow");

		lblDuration = new JLabel("Duration");
		lblDuration.setHorizontalAlignment(JLabel.TRAILING);
		lblDuration.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelReserveAndPayment.add(lblDuration, "cell 0 2,alignx trailing");

		txtDuration = new JTextField();
		txtDuration.setForeground(Color.WHITE);
		txtDuration.setText("Duration");
		txtDuration.setHorizontalAlignment(JTextField.TRAILING);
		txtDuration.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		txtDuration.setColumns(10);
		panelReserveAndPayment.add(txtDuration, "cell 1 2,grow");

		JButton btnRefreshInfo = new JButton("Refresh");
		btnRefreshInfo.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelPayment.add(btnRefreshInfo, "cell 0 12 2 1,grow");

		JButton btnPay = new JButton("Pay");
		btnPay.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		panelPayment.add(btnPay, "cell 2 12,grow");

		JLabel lblWarning = new JLabel(
				"The filter information above is just the first search result, the more accurate the filter information, the more accurate the result.");
		lblWarning.setForeground(Color.ORANGE);
		lblWarning.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		add(lblWarning, "cell 0 2 3 1,alignx right");
	}

	private MenuController menuController = new MenuController();

	private volatile boolean isLoading = false;

	/**
	 * Loads data based on the search term.
	 * 
	 * @param searchTerm the term to search for menus.
	 */
	private void loadData(String type, String searchTerm) {
		if (isLoading) {
			return;
		}
		isLoading = true;

		ExecutorService executor = Executors.newSingleThreadExecutor(); // Sử dụng một luồng đơn
		CountDownLatch latch = new CountDownLatch(1); // Chỉ cần 1 latch cho một tác vụ

		executor.submit(() -> {
			try {
				if ("menus".equals(type)) {
					List<Menu> menus = fetchMenus(searchTerm);
					if (menus == null || menus.isEmpty()) {
						Toast.show(this, Toast.Type.INFO, "No menu in database or in search");
						return;
					}
					SwingUtilities.invokeLater(() -> {
						cardPanel.removeAll();
						populateCardMenu(menus);
					});
				} else if ("tables".equals(type)) {
					List<Table> tables = fetchTables(searchTerm);
					if (tables == null || tables.isEmpty()) {
						Toast.show(this, Toast.Type.INFO, "No tables in database or in search");
						return;
					}
					SwingUtilities.invokeLater(() -> {
						cardPanel.removeAll();
						populateCardTable(tables);
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				latch.countDown(); // Giảm latch
			}
		});

		new Thread(() -> {
			try {
				latch.await(); // Chờ cho đến khi latch được giảm
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				executor.shutdown();
				System.gc();
				isLoading = false; // Đặt isLoading thành false
			}
		}).start();
	}

	private List<Menu> fetchMenus(String searchTerm) throws IOException {
		if (searchTerm != null && !searchTerm.isEmpty()) {
			return menuController.searchMenus(searchTerm);
		} else {
			return menuController.getAllMenus();
		}
	}

	private List<Table> fetchTables(String searchTerm) throws IOException {
		// Giả sử bạn có phương thức trong MenuController để lấy danh sách bàn
		if (searchTerm != null && !searchTerm.isEmpty()) {
			return tableController.searchTables(searchTerm); // Thay thế bằng phương thức thực tế của bạn
		} else {
			return tableController.getAllTables(); // Thay thế bằng phương thức thực tế của bạn
		}
	}

	/**
	 * Populates the card menu with the fetched Menu objects.
	 * 
	 * @param menus The list of Menu objects to populate.
	 * @throws IOException If an I/O error occurs during population.
	 */
	private void populateCardMenu(List<Menu> menus) {
		SwingWorker<Void, CardMenu> cardWorker = new SwingWorker<Void, CardMenu>() {
			@Override
			protected Void doInBackground() throws Exception {
				List<List<Menu>> groupedMenus = sortAndGroupMenus(menus);
				for (List<Menu> group : groupedMenus) {
					for (Menu menu : group) {
						CardMenu cardMenu = new CardMenu(menu);
						System.out.println("Loading card: " + menu.getName());

						// Add MouseListener to cardMenu
						cardMenu.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseReleased(MouseEvent e) {
								if (e.getComponent() instanceof CardMenu) {
									if (SwingUtilities.isLeftMouseButton(e)) {
										cardMenu.setSelected(!cardMenu.isSelected());
										if (cardMenu.isSelected()) {
											cardMenu.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
										} else {
											cardMenu.setBorder(BorderFactory.createEmptyBorder());
										}
									} else if (e.isPopupTrigger()) {
										cardMenu.setSelected(true);
										cardMenu.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
										createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
									}
								}
							}
						});

						publish(cardMenu); // Send cardMenu to the process method
					}
				}
				return null;
			}

			@Override
			protected void process(List<CardMenu> chunks) {
				for (CardMenu cardMenu : chunks) {
					cardPanel.add(cardMenu, "growx"); // Add to the cardPanel with growx constraint
				}
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(() -> {
					cardPanel.revalidate();
					cardPanel.repaint();
					panelList.setViewportView(cardPanel);
					panelList.revalidate();
					panelList.repaint();
				});
			}
		};

		cardWorker.execute();
	}

	private void populateCardTable(List<Table> tables) {
		SwingWorker<Void, CardTable> tableWorker = new SwingWorker<Void, CardTable>() {
			@Override
			protected Void doInBackground() throws Exception {
				for (Table table : tables) {
					CardTable cardTable = new CardTable(table);
					System.out.println("Loading card: " + table.getName());

					// Add MouseListener to cardTable
					cardTable.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							if (e.getComponent() instanceof CardTable) {
								if (SwingUtilities.isLeftMouseButton(e)) {
									cardTable.setSelected(!cardTable.isSelected());
									if (cardTable.isSelected()) {
										cardTable.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
									} else {
										cardTable.setBorder(BorderFactory.createEmptyBorder());
									}
								} else if (e.isPopupTrigger()) {
									cardTable.setSelected(true);
									cardTable.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
									createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
								}
							}
						}
					});

					publish(cardTable); // Send cardTable to the process method
				}
				return null;
			}

			@Override
			protected void process(List<CardTable> chunks) {
				for (CardTable cardTable : chunks) {
					cardPanel.add(cardTable, "growx"); // Add to the cardPanel with growx constraint
				}
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(() -> {
					cardPanel.revalidate();
					cardPanel.repaint();
					panelList.setViewportView(cardPanel);
					panelList.revalidate();
					panelList.repaint();
				});
			}
		};

		tableWorker.execute();
	}

	private List<List<Menu>> sortAndGroupMenus(List<Menu> menus) {
		List<Menu> availableMenus = new ArrayList<>();
		List<Menu> otherMenus = new ArrayList<>();

		for (Menu menu : menus) {
			if ("AVAILABLE".equalsIgnoreCase(menu.getStatus().getDisplayName())) {
				availableMenus.add(menu);
			} else {
				otherMenus.add(menu);
			}
		}

		availableMenus.sort(Comparator.comparing(Menu::getName));
		otherMenus.sort(Comparator.comparing(Menu::getName));

		List<List<Menu>> groupedMenus = new ArrayList<>();
		groupedMenus.add(availableMenus);
		groupedMenus.add(otherMenus);
		return groupedMenus;
	}

	/**
	 * Creates a popup menu for the table.
	 * 
	 * @return the created JPopupMenu for the table
	 */
	public JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem detailsMenuItem = new JMenuItem("View Details");
		JMenuItem addToReservationMenuItem = new JMenuItem("Add to Reservation");
		popupMenu.add(detailsMenuItem);
		popupMenu.add(addToReservationMenuItem);
		detailsMenuItem.addActionListener(e -> buttonAction("details"));
		addToReservationMenuItem.addActionListener(e -> buttonAction("add"));
		return popupMenu;
	}

	private String userAction;

	public void buttonAction(String userAction) {
		switch (userAction) {
		case "menus":
			loadData("menus", ""); // Tải dữ liệu thực đơn
			break;
		case "tables":
			loadData("tables", ""); // Tải dữ liệu bàn
			break;
		case "menu-details":
			showMenuDetailsModal();
			break;
		case "table-details":
			showTableDetailsModal();
			break;
		case "add":
			addAction();
			break;
		default:
			break;
		}
	}

	private void addAction() {

	}

	private TableController tableController = new TableController();

	private void loadTableData() {
		// Tạo một ExecutorService để tải dữ liệu bàn
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			try {
				// Giả sử bạn có một phương thức trong MenuController để lấy danh sách bàn
				List<Table> tables = tableController.getAllTables(); // Thay thế bằng phương thức thực tế của bạn
				SwingUtilities.invokeLater(() -> {
					// Xử lý và hiển thị danh sách bàn
					if (tables != null && !tables.isEmpty()) {
						// Tạo một JPanel hoặc JTable để hiển thị danh sách bàn
						JPanel tablePanel = new JPanel();
						tablePanel.setLayout(new MigLayout("wrap 1", "[grow]", ""));
						for (Table table : tables) {
							JLabel tableLabel = new JLabel(table.getName()); // Giả sử Table có phương thức getName()
							tablePanel.add(tableLabel);
						}
						panelList.setViewportView(tablePanel);
					} else {
						panelList.setViewportView(new JLabel("No tables available."));
					}
					panelList.revalidate();
					panelList.repaint();
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				executor.shutdown();
			}
		});
	}

	/**
	 * Displays a modal dialog for showing details of a selected menu.
	 */
	private void showMenuDetailsModal() {
		String[] idHolder = { "" };
		if (!validateSingleMenuCardSelection(idHolder, "view menu details"))
			return;
		MenuInfoForm infothis = createMenuInfo(idHolder[0]);
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(infothis, "Menu details information",
				AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
				}), DefaultComponent.getInfoForm());
	}

	private void showTableDetailsModal() {
		long[] idHolder = { -1L };
		if (!validateSingleTableCardSelection(idHolder, "view table details"))
			return;
		TableInfoForm infothis = createTableInfo(idHolder[0]);
		ModalDialog.showModal(this, new AdaptSimpleModalBorder(infothis, "Table details information",
				AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
				}), DefaultComponent.getInfoForm());
	}

	/**
	 * Validates if a single card is selected in the grid table.
	 * 
	 * @param idHolder The array to hold the selected menu ID.
	 * @param action   The action to perform (view details, edit, etc.).
	 * @return True if a single card is selected, false otherwise.
	 */
	private boolean validateSingleMenuCardSelection(String[] idHolder, String action) {
		List<String> selectedIds = findSelectedMenuIds(cardPanel);
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

	private boolean validateSingleTableCardSelection(long[] idHolder, String action) {
		List<Long> selectedIds = findSelectedTableIds(cardPanel);
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

	private MenuInfoForm createMenuInfo(String menuId) {
		try {
			return new MenuInfoForm(menuId);
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to find menu to view details: " + e.getMessage());
			return null;
		}
	}

	private TableInfoForm createTableInfo(long tableId) {
		try {
			return new TableInfoForm(tableId);
		} catch (IOException e) {
			Toast.show(this, Toast.Type.ERROR, "Failed to find table to view details: " + e.getMessage());
			return null;
		}
	}

	public List<String> findSelectedMenuIds(JPanel panelCard) {
		List<String> selectedMenuIds = new ArrayList<>();
		Component[] components = panelCard.getComponents();
		List<List<Component>> chunks = createComponentMenuChunks(components);
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<Callable<List<String>>> tasks = new ArrayList<>();

		for (List<Component> chunk : chunks) {
			tasks.add(() -> processComponentMenuChunk(chunk));
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

	public List<Long> findSelectedTableIds(JPanel panelCard) {
		List<Long> selectedTableIds = new ArrayList<>();
		Component[] components = panelCard.getComponents();
		List<List<Component>> chunks = createComponentTableChunks(components);
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<Callable<List<Long>>> tasks = new ArrayList<>();

		for (List<Component> chunk : chunks) {
			tasks.add(() -> processComponentTableChunk(chunk));
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

	private static final int CHUNK_SIZE = 4;

	private List<List<Component>> createComponentMenuChunks(Component[] components) {
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
	 * Creates chunks of components for processing.
	 * 
	 * @param components the array of components to chunk
	 * @return a list of chunks containing components
	 */
	private List<List<Component>> createComponentTableChunks(Component[] components) {
		List<List<Component>> chunks = new ArrayList<>();
		List<Component> currentChunk = new ArrayList<>();

		for (Component component : components) {
			currentChunk.add(component);

			if (currentChunk.size() == 4) {
				chunks.add(new ArrayList<>(currentChunk));
				currentChunk.clear();
			}
		}

		if (!currentChunk.isEmpty()) {
			chunks.add(currentChunk);
		}

		return chunks;
	}

	private List<String> processComponentMenuChunk(List<Component> chunk) {
		List<String> selectedIds = new ArrayList<>();

		for (Component component : chunk) {
			if (component instanceof CardMenu) {
				CardMenu cardMenu = (CardMenu) component;
				if (cardMenu.isSelected()) {
					selectedIds.add(cardMenu.getModel().getId());
				}
			}
		}

		return selectedIds;
	}

	private List<Long> processComponentTableChunk(List<Component> chunk) {
		List<Long> selectedIds = new ArrayList<>();

		for (Component component : chunk) {
			if (component instanceof CardTable) {
				CardTable cardTable = (CardTable) component;
				if (cardTable.isSelected()) {
					selectedIds.add(cardTable.getModel().getId());
				}
			}
		}

		return selectedIds;
	}
}