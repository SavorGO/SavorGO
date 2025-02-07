package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form;

import com.formdev.flatlaf.FlatClientProperties;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.layout.ResponsiveLayout;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller.UserFormController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.CheckBoxTableHeaderRenderer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.TableHeaderAlignment;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.TableThumbnailRenderer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.SystemForm;
import net.miginfocom.swing.MigLayout;

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

@SystemForm(name = "User Management", description = "User Management is a user interface component", tags = { "user", "management" })
public class UserFormUI extends Form {
    private UserFormController controller;
    private final Object columns[] = new Object[] { "SELECT", "#","THUMBNAIL", "EMAIL", "ROLE", "POINTS", "TIER", "CREATED TIME", "MODIFIED TIME" };
    private DefaultTableModel tableModel = createTableModel();
    private JTable table = new JTable(tableModel);
    private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT, new Dimension(-1, -1), 10, 10);
    private JPanel panelCard = new JPanel(responsiveLayout);
    private String selectedTitle = "User Table";

    /**
     * Constructs a UserFormUI and initializes the controller.
     */
    public UserFormUI() {
        controller = new UserFormController(this);
        init();
    }

    /**
     * Initializes the UI components and layout.
     */
    private void init() {
        setLayout(new MigLayout("fillx,wrap", "[fill]", "[][fill,grow]"));
        add(createInfoPanel("User Management", "This interface allows users to manage user accounts, including viewing, editing, and deleting user information.", 1));
        add(createTabPanel(), "gapx 7 7");
    }

    /**
     * Creates an information panel with a title and description.
     * 
     * @param title the title of the panel
     * @param description the description of the panel
     * @param level the level of the title for font size adjustment
     * @return the created JPanel containing the title and description
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
     * Creates a tab panel with basic and grid tables.
     * 
     * @return the created JTabbedPane containing the user table
     */
    private Component createTabPanel() {
        JTabbedPane tabb = new JTabbedPane();
        tabb.putClientProperty(FlatClientProperties.STYLE, "" + "tabType:card");
        tabb.addTab("Basic Table", createBorder(createBasicTable()));
        tabb.addTab("Grid table", createBorder(createGridTable()));        tabb.addChangeListener(e -> {
            controller.loadData("");
            selectedTitle = tabb.getTitleAt(tabb.getSelectedIndex());
        });
        return tabb;
    }

    /**
     * Creates a bordered panel for the specified component.
     * 
     * @param component the component to be added to the bordered panel
     * @return the created JPanel with a border around the component
     */
    private Component createBorder(Component component) {
        JPanel panel = new JPanel(new MigLayout("fill,insets 7 0 7 0", "[fill]", "[fill]"));
        panel.add(component);
        return panel;
    }

    /**
     * Creates the user table panel.
     * 
     * @return the created JPanel containing the user table
     */
    private Component createBasicTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
        configureTableProperties();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        styleTableWithFlatLaf(scrollPane);
        panel.add(createTableTitle("User Management"), "gapx 20");
        panel.add(createHeaderActionPanel());
        panel.add(scrollPane);
        controller.loadData("");
        return panel;
    }

    /**
     * Configures the properties of the table.
     */
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
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);
        table.setDefaultRenderer(ThumbnailCell.class, new TableThumbnailRenderer(table));
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
            @Override
            protected int getAlignment(int column) {
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
    
    /**
     * Styles the table with FlatLaf properties.
     * 
     * @param scrollPane the JScrollPane containing the table
     */
    private void styleTableWithFlatLaf(JScrollPane scrollPane) {
        panelCard.putClientProperty(FlatClientProperties.STYLE, "" + "arc:20;" + "background:$Table.background;");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "" + "height:30;" + "hoverBackground:null;" + "pressedBackground:null;" + "separatorColor:$TableHeader.background;");
        table.putClientProperty(FlatClientProperties.STYLE, "" + "rowHeight:70;" + "showHorizontalLines:true;" + "intercellSpacing:0,1;" + "cellFocusColor:$TableHeader.hoverBackground;" + "selectionBackground:$TableHeader.hoverBackground;" + "selectionInactiveBackground:$TableHeader.hoverBackground;" + "selectionForeground:$Table.foreground;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" + "trackArc:$ScrollBar.thumbArc;" + "trackInsets:3,3,3,3;" + "thumbInsets:3,3,3,3;" + "background:$Table.background;");
    }

    /**
     * Creates a title label for the table.
     * 
     * @param title the title of the table
     * @return the created JLabel with the title
     */
    private JLabel createTableTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        return titleLabel;
    }
    
    /**
     * Creates the grid table panel.
     * 
     * @return the created JPanel containing the grid table
     */
    private Component createGridTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
        configurePanelCardStyle();
        JScrollPane scrollPane = createScrollPaneForPanelCard();
        panel.add(createTableTitle("Grid Menu"), "gapx 20");
        panel.add(createHeaderActionPanel());
        panel.add(scrollPane);
        controller.loadData("");
        return panel;
    }
    
    /**
     * Configures the style of the panel card.
     */
    private void configurePanelCardStyle() {
        panelCard.putClientProperty(FlatClientProperties.STYLE, "border:10,10,10,10;");
    }
    
    /**
     * Creates a scroll pane for the panel card.
     * 
     * @return the created JScrollPane containing the panel card
     */
    private JScrollPane createScrollPaneForPanelCard() {
        JScrollPane scrollPane = new JScrollPane(panelCard);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc:$ScrollBar.thumbArc; thumbInsets:0,0,0,0; width:5;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc:$ScrollBar.thumbArc; thumbInsets:0,0,0,0; width:5;");
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
    /**
     * Creates a header action panel with buttons and search field.
     * 
     * @return the created JPanel containing the header action components
     */
    private Component createHeaderActionPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill,230]push[][]"));
        JTextField txtSearch = createSearchTextField();
        JButton cmdDetails = createButton("Details", e -> controller.showModal("details"));
        JButton cmdCreate = createButton("Create", e -> controller.showModal("create"));
        JButton cmdEdit = createButton("Edit", e -> controller.showModal("edit"));
        JButton cmdDelete = createButton("Delete", e -> controller.showModal("delete"));
        panel.add(txtSearch);
        panel.add(cmdDetails);
        panel.add(cmdCreate);
        panel.add(cmdEdit);
        panel.add(cmdDelete);
        panel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        return panel;
    }

    /**
     * Creates a popup menu for the table.
     * 
     * @return the created JPopupMenu for the table
     */
    public JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem detailsMenuItem = new JMenuItem("View Details");
        JMenuItem copyMenuItem = new JMenuItem("Copy cell text");
        JMenuItem editMenuItem = new JMenuItem("Edit");
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        popupMenu.add(detailsMenuItem);
        popupMenu.add(copyMenuItem);
        popupMenu.add(editMenuItem);
        popupMenu.add(deleteMenuItem);
        detailsMenuItem.addActionListener(e -> controller.showModal("details"));
        copyMenuItem.addActionListener(e -> copyAction());
        editMenuItem.addActionListener(e -> controller.showModal("edit"));
        deleteMenuItem.addActionListener(e -> controller.showModal("delete"));
        return popupMenu;
    }

    /**
     * Copies the selected cell text to the clipboard.
     */
    private void copyAction() {
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();
        if (selectedRow != -1 && selectedColumn != -1) {
            Object value = table.getValueAt(selectedRow, selectedColumn);
            if (value != null) {
                StringSelection stringSelection = new StringSelection(value.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(this, "Copied to clipboard", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Cell is empty", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a cell to copy", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a button with the specified text and action listener.
     * 
     * @param text the text of the button
     * @param actionListener the action listener for the button
     * @return the created JButton
     */
    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * Creates a search text field with a placeholder and leading icon.
     * 
     * @return the created JTextField for searching
     */
    private JTextField createSearchTextField() {
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.handleSearchTextChange(txtSearch);
            }
        });
        return txtSearch;
    }

    /**
     * Creates a table model for the JTable.
     * 
     * @return the created DefaultTableModel
     */
    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the select column is editable
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

    /**
     * Gets the JTable used in the form.
     * 
     * @return the JTable
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Gets the DefaultTableModel used in the form.
     * 
     * @return the DefaultTableModel
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Gets the title of the selected tab.
     * 
     * @return the title of the selected tab
     */
    public String getSelectedTitle() {
        return selectedTitle;
    }

    /**
     * Refreshes the table data by reloading it.
     */
    @Override
    public void formRefresh() {
        controller.loadData("");
    }
    
    /**
     * Gets the panel card that contains the CardTable components.
     * 
     * @return the JPanel containing the CardTable components
     */
    public JPanel getPanelCard() {
        return panelCard;
    }
    
    
}