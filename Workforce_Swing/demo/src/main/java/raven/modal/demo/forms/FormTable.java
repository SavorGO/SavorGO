package raven.modal.demo.forms;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder;
import raven.modal.demo.controllers.ControllerTable;
import raven.modal.demo.forms.input.InputFormCreateTable;
import raven.modal.demo.forms.input.InputFormUpdateTable;
import raven.modal.demo.forms.other.CardTable;
import raven.modal.demo.layout.ResponsiveLayout;
import raven.modal.demo.models.EnumTableStatus;
import raven.modal.demo.models.ModelProfile;
import raven.modal.demo.models.ModelTable;
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
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

@SystemForm(name = "Table", description = "table is a user interface component", tags = {"list"})
public class FormTables extends Form {
	private ControllerTable controllerTable = new ControllerTable(); // Controller for handling table operations
    private ServiceTable tableService = new ServiceImplTable();  // Service layer for table-related operations
    private final Object columns[] = new Object[]{"SELECT", "#", "NAME", "STATUS", "IS RESERVED", "CREATED TIME", "UPDATED TIME"};
    private DefaultTableModel tableModel =new DefaultTableModel(columns, 0) { //Table model for managing rows and columns
        @Override
        public boolean isCellEditable(int row, int column) {
            // allow cell editable at column 0 for checkbox
            return column == 0;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) return Boolean.class; // Boolean for checkbox
            return super.getColumnClass(columnIndex);
        }
    };
    private JTable table = new JTable(tableModel);
    private final ResponsiveLayout responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT, new Dimension(-1, -1), 10, 10);
    private JPanel panelCard = new JPanel(responsiveLayout);
    private static final int DEBOUNCE_DELAY = 1000; // Thời gian chờ (500ms)
    private Timer debounceTimer;
    
    public FormTables() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx,wrap", "[fill]", "[][fill,grow]"));
        add(createInfo("Table Management", "This is a user interface component that displays a collection of tables in a structured, tabular format. It allows users to view, sort, and manage data or other resources.", 1));
        add(createTab(), "gapx 7 7");
    }

    private JPanel createInfo(String title, String description, int level) {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap", "[fill]"));
        JLabel lbTitle = new JLabel(title);
        JTextPane text = new JTextPane();
        text.setText(description);
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder());
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +" + (4 - level));
        panel.add(lbTitle);
        panel.add(text, "width 500");
        return panel;
    }

    private Component createTab() {
        JTabbedPane tabb = new JTabbedPane();
        tabb.putClientProperty(FlatClientProperties.STYLE, "" +
                "tabType:card");
        tabb.addTab("Basic table", createBorder(createBasicTable()));
        tabb.addTab("Grid table", createBorder(createGridTable()));
        return tabb;
    }

    private Component createBorder(Component component) {
        JPanel panel = new JPanel(new MigLayout("fill,insets 7 0 7 0", "[fill]", "[fill]"));
        panel.add(component);
        return panel;
    }

    private Component createBasicTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
        table.setModel(tableModel);
        // Set table properties
        table.setGridColor(Color.LIGHT_GRAY); // Grid color
        table.setShowGrid(true); // Enable grid
        table.setRowHeight(50); // Row height
        table.setSelectionBackground(new Color(220, 230, 241)); // Selection background
        table.setSelectionForeground(Color.BLACK); // Selection foreground
        table.getTableHeader().setReorderingAllowed(false); // Disable reordering columns

        // Configure column widths
        table.getColumnModel().getColumn(0).setMaxWidth(50); // Checkbox
        table.getColumnModel().getColumn(1).setMaxWidth(50); // #
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Created Time
        table.getColumnModel().getColumn(6).setPreferredWidth(250); // Updated Time

        // Apply custom cell renderer for specific columns
        table.setDefaultRenderer(ModelProfile.class, new TableProfileCellRenderer(table));
        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));

        // Customize table header alignment
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
            @Override
            protected int getAlignment(int column) {
                return column == 1 ? SwingConstants.CENTER : SwingConstants.LEADING;
            }
        });

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Style table with FlatLaf properties
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:$Table.background;");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:30;");
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:50; showHorizontalLines:true;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc:12;");

        // Create title
        JLabel title = new JLabel("Grid Table Example");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        panel.add(title, "gapx 20");

        // Add header and scroll pane
        panel.add(createHeaderAction());
        panel.add(scrollPane);

        loadData("");
        return panel;
    }
    private Component createGridTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 10 10 10", "[fill]", "[][]0[fill,grow]"));
        
        // Áp dụng style cho panel
        panelCard.putClientProperty(FlatClientProperties.STYLE, "border:10,10,10,10;");
        
        // Tạo JScrollPane bao bọc panel
        JScrollPane scrollPane = new JScrollPane(panelCard);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:$ScrollBar.thumbArc;" +
                "thumbInsets:0,0,0,0;" +
                "width:5;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:$ScrollBar.thumbArc;" +
                "thumbInsets:0,0,0,0;" +
                "width:5;");
        scrollPane.setBorder(new TitledBorder("Example"));

        // Tạo JSplitPane để giữ giao diện
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(Box.createGlue());
        splitPane.setResizeWeight(1);
        // Create title
        JLabel title = new JLabel("Grid Table Example");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        panel.add(title, "gapx 20");
        panelCard.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:$Table.background;");
        panel.putClientProperty(FlatClientProperties.STYLE, "arc:20; background:$Table.background;");

        
        panel.add(createHeaderAction());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(panelCard);
        panel.add(scrollPane);
        loadData("");
        return panel;
    }
    private Consumer<ModelTable> createEventCard() {
        return e -> {
            JOptionPane.showMessageDialog(this, "Đã click với card: " + e.getName());
        };
    }

    private Component createHeaderAction() {
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill,230]push[][]"));

        JTextField txtSearch = createSearchTextField();
        
        JButton cmdCreate = new JButton("Create");
        JButton cmdEdit = new JButton("Edit");
        JButton cmdDelete = new JButton("Delete");
        // mỗi khi nhập ký tự thì sự kiện
        cmdCreate.addActionListener(e -> showModal("create"));
        cmdEdit.addActionListener(e -> showModal("edit"));
		cmdDelete.addActionListener(e -> showModal("delete"));
        panel.add(txtSearch);
        panel.add(cmdCreate);
        panel.add(cmdEdit);
        panel.add(cmdDelete);

        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        return panel;
    }
    
    private JTextField createSearchTextField() {
		JTextField txtSearch = new JTextField();
		txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("raven/modal/demo/icons/search.svg", 0.4f));
    	 txtSearch.addKeyListener(new KeyAdapter() {
             @Override
             public void keyReleased(KeyEvent e) {
                 // Nếu timer đang chạy, hủy nó và tạo lại
                 if (debounceTimer != null && debounceTimer.isRunning()) {
                     debounceTimer.stop();
                 }

                 // Tạo lại Timer mới
                 debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> {
                     // Hàm này sẽ được gọi khi người dùng ngừng nhập trong DEBOUNCE_DELAY (1000ms)
                     String searchText = txtSearch.getText(); // Lấy giá trị mới nhất
                     System.out.println("Searching for: " + searchText); // In ra giá trị tìm kiếm
                     loadData(txtSearch.getText()); // Gọi loadData() với giá trị mới
                 });

                 debounceTimer.setRepeats(false); // Chỉ chạy một lần
                 debounceTimer.start(); // Bắt đầu timer
             }
		 });
		return txtSearch;
    }
    
    // Refresh form
    @Override
    public void formRefresh() {
	    loadData("");
    }

    // Load data into the table
    private void loadData(String searchTerm) {
    	try {
    	    // Lấy danh sách ModelTable từ controllerTable
    		// Làm mới bảng lưới (gridTable)
            panelCard.removeAll(); // Xóa tất cả các CardTable hiện có
            List<ModelTable> tables = null;
            if (searchTerm != null && !searchTerm.isEmpty()) {
            	tables = controllerTable.searchTables(searchTerm); 
            } else {
            	tables = controllerTable.getAllTables();
            }
            if(tables == null || tables.size() == 0) {
	            Toast.show(this, Toast.Type.INFO, "No table in database");
	            return;
            }
            tables.stream()
	          .sorted(Comparator.comparing(ModelTable::getId)) // Sắp xếp theo ID (hoặc thuộc tính khác nếu cần)
	          .forEach(modelTable -> panelCard.add(new CardTable(modelTable, createEventCard())));
            panelCard.revalidate(); // Yêu cầu bố cục lại
            panelCard.repaint(); // Vẽ lại giao diện
            tableModel.setRowCount(0);
    	    // Sử dụng Stream để duyệt qua danh sách và thêm hàng vào model
            tables.stream()
    	          .sorted(Comparator.comparing(ModelTable::getId)) // Sắp xếp theo ID (hoặc thuộc tính khác nếu cần)
    	          .forEach(modelTable -> tableModel.addRow(modelTable.toTableRowBasic()));
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    }
    
    // Create modal dialog for adding a new table
    private void showModal(String userAction) {

        switch (userAction) {
		case "create":
			InputFormCreateTable inputFormCreateTable = new InputFormCreateTable();
	        ModalDialog.showModal(this, new SimpleModalBorder(
	        		inputFormCreateTable, "Create table", SimpleModalBorder.YES_NO_OPTION,
	                (controller, action) -> {
	                    if (action == SimpleModalBorder.YES_OPTION) {
	                        String tableName = inputFormCreateTable.getTableName();
	                        try {
	                            controllerTable.createTable(tableName);
	                            Toast.show(this, Toast.Type.SUCCESS, "Create table successfully");
	                            loadData(""); // Reload table data after successful creation
	                        } catch (IOException e) {
	                            Toast.show(this, Toast.Type.ERROR, "Failed to create table: " + e.getMessage());
	                        }
	                    }
	                }), DefaultComponent.getInputForm());
			break;
		case "edit":
			// Break if no row selected
			if(table.getSelectedRowCount() == 0) {
				Toast.show(this, Toast.Type.ERROR, "Please select a row to edit");
				break;
			}
			// Break if many row selected
			if(table.getSelectedRowCount() > 1) {
				Toast.show(this, Toast.Type.ERROR, "Please select only one row to edit");
				break;
			}
			long id = (long) table.getValueAt(table.getSelectedRow(), 1);
			InputFormUpdateTable inputFormUpdateTable;
			try {
				inputFormUpdateTable = new InputFormUpdateTable(id);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.show(this, Toast.Type.ERROR, "Failed to find table to edit: "+ e.getMessage());
				break;
			}
	        ModalDialog.showModal(this, new SimpleModalBorder(
	        		inputFormUpdateTable, "Update table", SimpleModalBorder.YES_NO_OPTION,
	                (controller, action) -> {
	                    if (action == SimpleModalBorder.YES_OPTION) {
	                        String tableName = inputFormUpdateTable.getTableName();
	                        LocalDateTime revervedTime;
	                        EnumTableStatus status = inputFormUpdateTable.getEnumTableStatus();
	                        try {
	                        	revervedTime = inputFormUpdateTable.getReservedTime();
	                            controllerTable.updateTable(id, tableName,status, revervedTime);
	                            Toast.show(this, Toast.Type.SUCCESS, "Update table successfully");
	                            loadData(""); // Reload table data after successful creation
	                        } catch (IOException | BusinessException e) {
	                            Toast.show(this, Toast.Type.ERROR, "Failed to update table: " + e.getMessage());
	                            e.printStackTrace();
	                        }
	                    }
	                }), DefaultComponent.getInputForm());
			break;
		case "delete":
			List<Long> deleteSelectedTables = deleteSelectedTables();
			switch (deleteSelectedTables.size()) {
			case 0:
				Toast.show(this, Toast.Type.ERROR, "You have to select at least one table to delete");
				break;
			case 1:
				ModalDialog.showModal(this, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete this table: "+deleteSelectedTables().toString()+"? This action cannot be undone.","Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
	                if (action == SimpleModalBorder.YES_OPTION) {	
	                	try {
							controllerTable.removeTable(deleteSelectedTables.getFirst());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Toast.show(this, Toast.Type.ERROR, "Failed to delete tables: " + e.getMessage());
						}
	                	loadData(""); // Reload table data after successful creation
	                	
	                	//success
	                	Toast.show(this, Toast.Type.SUCCESS, "Remove table successfully");
	                }}), DefaultComponent.getChoiceModal());
				break;

			default:
				ModalDialog.showModal(this, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete these tables: "+deleteSelectedTables().toString()+"? This action cannot be undone.","Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
	                if (action == SimpleModalBorder.YES_OPTION) {	
	                	try {
							controllerTable.removeTables(deleteSelectedTables);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Toast.show(this, Toast.Type.ERROR, "Failed to delete tables: " + e.getMessage());
						}
	                	loadData(""); // Reload table data after successful creation
	                	//success
	                	Toast.show(this, Toast.Type.SUCCESS, "Remove table successfully");
	                }}), DefaultComponent.getChoiceModal());
				break;
			}
			break;
		default:
			break;
		}
        
    }
    private static final int CHUNK_SIZE = 4; // Kích thước mỗi đơn vị (chunk)

    private List<Long> deleteSelectedTables() {
        int rowCount = table.getRowCount();
        List<Long> tableIdsToDelete = new ArrayList<>();

        // Chia bảng thành các phần nhỏ hơn, mỗi phần chứa tối thiểu 4 dòng
        List<List<Long>> chunks = createChunks(rowCount);

        // Sử dụng ExecutorService để xử lý các phần song song
        ExecutorService executorService = Executors.newFixedThreadPool(4); // Sử dụng tối đa 4 luồng
        List<Callable<List<Long>>> tasks = new ArrayList<>();
        
        // Tạo các tác vụ xử lý từng chunk
        for (List<Long> chunk : chunks) {
            tasks.add(() -> {
                // Xử lý từng phần nhỏ (chunk) và trả về danh sách các tableId cần xóa
                return processChunk(chunk);
            });
        }

        try {
            // Thực thi các tác vụ đồng thời và lấy kết quả
            List<Future<List<Long>>> results = executorService.invokeAll(tasks);

            // Kết hợp kết quả từ tất cả các luồng
            for (Future<List<Long>> result : results) {
                tableIdsToDelete.addAll(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return tableIdsToDelete;
    }

    // Tách bảng thành các phần nhỏ (chunk) với kích thước tối thiểu là 4 dòng
    private List<List<Long>> createChunks(int rowCount) {
        List<List<Long>> chunks = new ArrayList<>();
        List<Long> currentChunk = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Long tableId = (Long) table.getValueAt(i, 1); // Cột 1 chứa ID của bảng
            currentChunk.add(tableId);

            // Nếu mỗi chunk có tối thiểu 4 dòng, thêm vào danh sách và tạo chunk mới
            if (currentChunk.size() == CHUNK_SIZE) {
                chunks.add(new ArrayList<>(currentChunk));
                currentChunk.clear(); // Xóa chunk cũ để tạo chunk mới
            }
        }

        // Nếu còn dư dòng (ít hơn 4 dòng), thêm vào chunk cuối cùng
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk);
        }

        return chunks;
    }

    // Xử lý một phần nhỏ (chunk) trong bảng và trả về danh sách các tableId cần xóa
    private List<Long> processChunk(List<Long> chunk) {
        List<Long> tableIdsToDelete = new ArrayList<>();
        
        // Duyệt qua từng phần trong chunk và kiểm tra giá trị ở cột 0
        for (Long tableId : chunk) {
            // Tìm vị trí dòng của tableId
            int rowIndex = table.getRowCount() - 1;
            for (int i = 0; i < table.getRowCount(); i++) {
                if (table.getValueAt(i, 1).equals(tableId)) {
                    rowIndex = i;
                    break;
                }
            }

            // Kiểm tra giá trị ở cột 0 (có phải false không)
            Boolean isChecked = (Boolean) table.getValueAt(rowIndex, 0); // Cột 0 chứa giá trị boolean
            if (isChecked != null && isChecked) {
                // Nếu giá trị ở cột 0 là false, thêm vào danh sách cần xóa
                tableIdsToDelete.add(tableId);
            }
        }

        return tableIdsToDelete;
    }
}