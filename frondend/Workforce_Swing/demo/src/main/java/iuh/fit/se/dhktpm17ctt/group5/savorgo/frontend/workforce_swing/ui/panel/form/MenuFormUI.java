package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.layout.ResponsiveLayout;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller.MenuFormController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.CheckBoxTableHeaderRenderer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.TableHeaderAlignment;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.TableThumbnailRenderer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.SystemForm;
import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

@SystemForm(name = "Menu", description = "Menu is a user interface component", tags = { "list" })
public class MenuFormUI extends Form {
	private MenuFormController controller;
	private final Object columns[] = new Object[] { "SELECT", "#", "THUMBNAIL", "CATEGORY", "ORIGINAL PRICE",
			"SALE PRICE", "DISCOUNTED PRICE", "CREATED TIME", "UPDATED TIME" };
	private DefaultTableModel tableModel = createTableModel();
	private JTable table = new JTable(tableModel);
	private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT,
			new Dimension(-1, -1), 10, 10);
	private JPanel panelCard = new JPanel(responsiveLayout);
	private static final int DEBOUNCE_DELAY = 1000;
	private Timer debounceTimer;
	private String selectedTitle = "Basic table";

	public MenuFormUI() {
		controller = new MenuFormController(this);
		init();
	}

	private void init() {
		setLayout(new MigLayout("fillx,wrap", "[fill]", "[][fill,grow]"));
		add(createInfoPanel("Menu Management",
				"This is a user interface component that displays a collection of menus in a structured, tabular format. It allows users to view, sort, and manage data or other resources.",
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
			controller.reloadData();
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
		JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]10[fill,grow]"));
		configureTableProperties();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		styleTableWithFlatLaf(scrollPane);
		panel.add(createTableTitle("Basic Table"), "gapx 20");
		panel.add(createHeaderActionPanel());
		panel.add(scrollPane);
		controller.reloadData();
		return panel;
	}

	private void configureTableProperties() {
		table.setModel(tableModel);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setRowHeight(50);
		table.setSelectionBackground(new Color(220, 230, 241));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMaxWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setPreferredWidth(150);
		table.getColumnModel().getColumn(5).setPreferredWidth(150);
		table.getColumnModel().getColumn(6).setPreferredWidth(250);
		table.getColumnModel().getColumn(7).setPreferredWidth(250);
		table.setDefaultRenderer(ThumbnailCell.class, new TableThumbnailRenderer(table));
		table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
		table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
			@Override
			protected int getAlignment(int column) {
				int trailing[] = { 5, 6, 7, 8 };
				int center[] = { 2 };
				if (Arrays.stream(trailing).anyMatch(value -> value == column)) {
					return SwingConstants.TRAILING;
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
						table.repaint();
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
		JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]10[fill,grow]"));
		configureTableProperties();
		configurePanelCardStyle();
		JScrollPane scrollPane = createScrollPaneForPanelCard();
		panel.add(createTableTitle("Grid Table"), "gapx 20");
		panel.add(createHeaderActionPanel());
		panel.add(scrollPane);
		controller.reloadData();
		return panel;
	}

	private void configurePanelCardStyle() {
		panelCard.putClientProperty(FlatClientProperties.STYLE, "border:10,10,10,10;");
	}

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

	private Component createHeaderActionPanel() {
		// Using MigLayout with vertical gap (gapy) 10px between 2 rows.
		JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20, gapy 10, gapx 10", "[fill]", "[]10[]"));

		// Row 1: Search, pagination controls
		JTextField txtSearch = createSearchTextField();
		JButton btnSearch = new JButton("Search");

		int totalPages = controller.getTotalPages();

		JSpinner spnCurrentPage = new JSpinner(new SpinnerNumberModel(1, 1, totalPages, 1));
		spnCurrentPage.setPreferredSize(new Dimension(50, 25));

		JButton btnFirst = new JButton("<<");
		btnFirst.setPreferredSize(new Dimension(50, 25));

		JTextField txtTotalPages = new JTextField("/   " + totalPages);
		txtTotalPages.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add border to mimic spinner
		txtTotalPages.setHorizontalAlignment(SwingConstants.LEFT);
		txtTotalPages.setPreferredSize(new Dimension(50, 25));
		txtTotalPages.setEditable(false);
		txtTotalPages.setBorder(null);

		JButton btnLast = new JButton(">>");
		btnLast.setPreferredSize(new Dimension(50, 25));

		// Add row 1 components
		panel.add(txtSearch, "growx, pushx");
		panel.add(btnSearch);
		panel.add(btnFirst);
		panel.add(spnCurrentPage, "gapx 5");
		panel.add(txtTotalPages, "gapx 5");
		panel.add(btnLast, "wrap");

		// Row 2: Checkbox and action buttons
		JCheckBox chkShowDeleted = new JCheckBox("Show Deleted Included");

		JButton cmdDetails = new JButton("Details");
		JButton cmdCreate = new JButton("Create");
		JButton cmdEdit = new JButton("Edit");
		JButton cmdDelete = new JButton("Delete");

		JSpinner spnPageSize = new JSpinner(new SpinnerNumberModel(10, 5, 100, 5));
		spnPageSize.setPreferredSize(new Dimension(50, 25));

		// Add row 2 components
		panel.add(chkShowDeleted);
		panel.add(cmdDetails);
		panel.add(cmdCreate);
		panel.add(cmdEdit);
		panel.add(cmdDelete);
		panel.add(spnPageSize);

		// Set action listeners at the end for better readability
		// MouseListener cho chuột phải để xóa nội dung
		txtSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) { // Kiểm tra nếu là chuột phải
					txtSearch.setText(""); // Xóa nội dung
				}
			}
		});

		txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				checkAndHandleSearch();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				checkAndHandleSearch();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				checkAndHandleSearch();
			}

			private void checkAndHandleSearch() {
				// Nếu txtSearch trống, gọi handleSearchButton
				if (!txtSearch.getText().trim().isEmpty()) {
					return;
				}
				controller.handleSearchButton(txtSearch, spnCurrentPage, txtTotalPages);
			}
		});

		btnSearch.addActionListener(e -> controller.handleSearchButton(txtSearch, spnCurrentPage, txtTotalPages));
		btnFirst.addActionListener(e -> controller.moveToFirst(spnCurrentPage, txtTotalPages));
		btnLast.addActionListener(e -> controller.moveToLast(spnCurrentPage, txtTotalPages));
		chkShowDeleted
				.addActionListener(e -> controller.checkChkShowDeleted(chkShowDeleted, spnCurrentPage, txtTotalPages));

		cmdDetails.addActionListener(e -> controller.showModal("details"));
		cmdCreate.addActionListener(e -> controller.showModal("create"));
		cmdEdit.addActionListener(e -> controller.showModal("edit"));
		cmdDelete.addActionListener(e -> controller.showModal("delete"));

		spnCurrentPage.addChangeListener(e -> controller.moveToPage(spnCurrentPage));
		spnPageSize.addChangeListener(e -> controller.changePageSize(spnPageSize, spnCurrentPage, txtTotalPages));

		panel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
		return panel;
	}

	public JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem detailsMenuItem = new JMenuItem("View Details");
		JMenuItem copyMenuItem = new JMenuItem("Copy cell text");
		JMenuItem editMenuItem = new JMenuItem("Edit");
		JMenuItem deleteMenuItem = new JMenuItem((controller.findSelectedMenuIds().size() == 1
				|| (controller.findSelectedMenuIds(panelCard).size() == 1)) ? "Delete" : "Delete many");
		JMenuItem createPromotionItem = new JMenuItem((controller.findSelectedMenuIds().size() == 1
				|| (controller.findSelectedMenuIds(panelCard).size() == 1)) ? "Create promotion" : "Create many promotions");
		popupMenu.add(detailsMenuItem);
		popupMenu.add(copyMenuItem);
		popupMenu.add(editMenuItem);
		popupMenu.add(deleteMenuItem);
		popupMenu.add(createPromotionItem);
		detailsMenuItem.addActionListener(e -> controller.showModal("details"));
		copyMenuItem.addActionListener(e -> copyAction());
		editMenuItem.addActionListener(e -> controller.showModal("edit"));
		deleteMenuItem.addActionListener(e -> controller.showModal("delete"));
		createPromotionItem.addActionListener(e -> controller.showModal("create-promotion"));
		return popupMenu;
	}

	private void copyAction() {
		int selectedRow = table.getSelectedRow();
		int selectedColumn = table.getSelectedColumn();
		if (selectedRow != -1 && selectedColumn != -1) {
			Object value = table.getValueAt(selectedRow, selectedColumn);
			if (value != null) {
				StringSelection stringSelection = new StringSelection(value.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				Toast.show(this, Toast.Type.SUCCESS, "Copied to clipboard");
			} else {
				Toast.show(this, Toast.Type.INFO, "Cell is empty");
			}
		} else {
			Toast.show(this, Toast.Type.ERROR, "Please select a cell to copy");
		}
	}

	private JButton createButton(String text, ActionListener actionListener) {
		JButton button = new JButton(text);
		button.addActionListener(actionListener);
		return button;
	}

	private JTextField createSearchTextField() {
		JTextField txtSearch = new JTextField();
		txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
		txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
				new FlatSVGIcon("raven/modal/demo/icons/search.svg", 0.4f));
		return txtSearch;
	}

	private DefaultTableModel createTableModel() {
		return new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 0;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return Boolean.class;
				if (columnIndex == 2) {
					return ThumbnailCell.class;
				}
				return super.getColumnClass(columnIndex);
			}
		};
	}

	public JTable getTable() {
		return table;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public JPanel getPanelCard() {
		return panelCard;
	}

	public String getSelectedTitle() {
		return selectedTitle;
	}

	@Override
	public void formRefresh() {
		controller.reloadData();
	}
}