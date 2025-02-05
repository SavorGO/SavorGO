package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.layout.ResponsiveLayout;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardPromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller.FormPromotionController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreatePromotionInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.PromotionInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.UpdatePromotionInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.CheckBoxTableHeaderRenderer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.TableHeaderAlignment;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.TableThumbnailRenderer;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.SystemForm;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

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
import java.io.IOException;
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

/**
 * User interface component for managing promotions.
 */
@SystemForm(name = "Promotion", description = "Promotion is a user interface component", tags = { "list" })
public class PromotionFormUI extends Form {
    private FormPromotionController controller;
    private final Object columns[] = new Object[] { "SELECT", "#", "THUMBNAIL", "VALUE", "START DATE", "END DATE", "MENU THUMBNAIL", "ORIGINAL PRICE", "SALE PRICE", "DISCOUNTED PRICE", "CREATED TIME", "UPDATED TIME" };
    private DefaultTableModel tableModel = createTableModel();
    private JTable table = new JTable(tableModel);
    private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT, new Dimension(-1, -1), 10, 10);
    private JPanel panelCard = new JPanel(responsiveLayout);
    private static final int DEBOUNCE_DELAY = 1000;
    private Timer debounceTimer;
    private String selectedTitle = "Basic table";

    /**
     * Constructs a PromotionFormUI instance and initializes the form.
     */
    public PromotionFormUI() {
        controller = new FormPromotionController(this);
        init();
    }

    /**
     * Initializes the layout and components of the promotion form.
     */
    private void init() {
        setLayout(new MigLayout("fillx,wrap", "[fill]", "[][fill,grow]"));
        add(createInfoPanel("Promotion Management", "This is a user interface component that displays a collection of promotions in a structured, tabular format. It allows users to view, sort, and manage data or other resources.", 1));
        add(createTabPanel(), "gapx 7 7");
    }

    /**
     * Creates an information panel with a title and description.
     *
     * @param title the title of the panel
     * @param description the description of the panel
     * @param level the level of the title's font size
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
     * Creates a tab panel for displaying different table views.
     *
     * @return the created JTabbedPane containing the table views
     */
    private Component createTabPanel() {
        JTabbedPane tabb = new JTabbedPane();
        tabb.putClientProperty(FlatClientProperties.STYLE, "" + "tabType:card");
        tabb.addTab("Basic table", createBorder(createBasicTable()));
        tabb.addTab("Grid table", createBorder(createGridTable()));
        tabb.addChangeListener(e -> {
            controller.loadData("");
            selectedTitle = tabb.getTitleAt(tabb.getSelectedIndex());
        });
        return tabb;
    }

    /**
     * Creates a bordered panel for a given component.
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
     * Creates a basic table for displaying promotions.
     *
     * @return the created JPanel containing the basic table
     */
    private Component createBasicTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
        configureTableProperties();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        styleTableWithFlatLaf(scrollPane);
        panel.add(createTableTitle("Basic Table"), "gapx 20");
        panel.add(createHeaderActionPanel());
        panel.add(scrollPane);
        controller.loadData("");
        return panel;
    }

    /**
     * Configures properties for the table.
     */
    private void configureTableProperties() {
        table.setModel(tableModel);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(220, 230, 241));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(true);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);
        table.getColumnModel().getColumn(5).setPreferredWidth(250);
        table.getColumnModel().getColumn(6).setPreferredWidth(250);
        table.getColumnModel().getColumn(7).setPreferredWidth(250);
        table.getColumnModel().getColumn(8).setPreferredWidth(250);
        table.getColumnModel().getColumn(9).setPreferredWidth(250);
        table.getColumnModel().getColumn(10).setPreferredWidth(250);
        table.setDefaultRenderer(ThumbnailCell.class, new TableThumbnailRenderer(table));
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
            @Override
            protected int getAlignment(int column) {
                int trailing[] = { 4, 5, 6, 7, 8, 9, 10 };
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

            /**
             * Displays a popup menu when the mouse is released.
             *
             * @param e the MouseEvent triggered by the mouse release
             */
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
     * @param title the title text
     * @return the created JLabel with the title
     */
    private JLabel createTableTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        return titleLabel;
    }

    /**
     * Creates a grid table for displaying promotions.
     *
     * @return the created JPanel containing the grid table
     */
    private Component createGridTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
        configurePanelCardStyle();
        JScrollPane scrollPane = createScrollPaneForPanelCard();
        panel.add(createTableTitle("Table Grid Table"), "gapx 20");
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
     * @return the created JPanel containing action buttons and search field
     */
    private Component createHeaderActionPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill,230]push[][]"));
        JTextField txtSearch = createSearchTextField();
        JButton cmdDetails = createButton("Details ", e -> controller.showModal("details"));
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
     * @return the created JPopupMenu with options for the selected row
     */
    public JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem detailsMenuItem = new JMenuItem("View Details");
        JMenuItem copyMenuItem = new JMenuItem("Copy cell text");
        JMenuItem editMenuItem = new JMenuItem("Edit");
        JMenuItem deleteMenuItem = new JMenuItem((controller.findSelectedPromotionIds().size() == 1 || (controller.findSelectedPromotionIds(panelCard).size() == 1)) ? "Delete" : "Delete many");
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
     * Copies the selected cell's text to the clipboard.
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
                Toast.show(this, Toast.Type.SUCCESS, "Copied to clipboard");
            } else {
                Toast.show(this, Toast.Type.INFO, "Cell is empty");
            }
        } else {
            Toast.show(this, Toast.Type.ERROR, "Please select a cell to copy");
        }
    }

    /**
     * Creates a button with a specified text and action listener.
     *
     * @param text the text to display on the button
     * @param actionListener the action listener to handle button clicks
     * @return the created JButton
     */
    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * Creates a search text field with a placeholder.
     *
     * @return the created JTextField for searching
     */
    private JTextField createSearchTextField() {
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("raven/modal/demo/icons/search.svg", 0.4f));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                controller.handleSearchTextChange(txtSearch);
            }
        });
        return txtSearch;
    }

    /**
     * Creates a table model for the promotions table.
     *
     * @return the created DefaultTableModel
     */
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
                if (columnIndex == 2 || columnIndex == 6) {
                    return ThumbnailCell.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
    }

    /**
     * Gets the table component.
     *
     * @return the JTable used in the form
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Gets the table model.
     *
     * @return the DefaultTableModel used in the form
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Gets the panel card component.
     *
     * @return the JPanel used for the card layout
     */
    public JPanel getPanelCard() {
 return panelCard;
    }

    /**
     * Gets the selected title of the tab.
     *
     * @return the title of the currently selected tab
     */
    public String getSelectedTitle() {
        return selectedTitle;
    }
}