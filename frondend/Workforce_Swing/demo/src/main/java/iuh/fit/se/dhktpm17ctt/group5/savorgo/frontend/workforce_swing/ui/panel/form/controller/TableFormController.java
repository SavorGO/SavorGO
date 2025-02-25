package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.TableController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.TableFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreateTableInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.TableInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.UpdateTableInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;

import org.checkerframework.checker.units.qual.t;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TableFormController {
	private TableFormUI tableFormUI;
	private TableController tableController = new TableController();
	private static final int DEBOUNCE_DELAY = 1000;
	private Timer debounceTimer;
	private String searchTerm = "";

	public TableFormController(TableFormUI formTableUI) {
		this.tableFormUI = formTableUI;
	}

	private volatile boolean isLoading = false;
	private int currentPage = 1;
	private boolean isShowDeleted = false;
	private Integer totalPages = Integer.MAX_VALUE;
	private int pageSize;

	public void loadData() {
		if (isLoading)
			return;
		isLoading = true;
		ExecutorService executor = Executors.newFixedThreadPool(2);
		CountDownLatch latch = new CountDownLatch(2);

		executor.submit(() -> {
			try {
				tableFormUI.getPanelCard().removeAll();
				List<Table> tables = fetchTables();
				if (tables == null || tables.isEmpty()) {
					Toast.show(tableFormUI, Toast.Type.INFO, "No table in database or in search");
					return;
				}
				SwingUtilities.invokeLater(() -> {
					tableFormUI.getPanelCard().removeAll();
					populateCardTable(tables);
				});
				populateCardTable(tables);
			} finally {
				latch.countDown();
			}
		});

		executor.submit(() -> {
			try {
				List<Table> tables = fetchTables();
				SwingUtilities.invokeLater(() -> {
					tableFormUI.getTableModel().setRowCount(0); // Clear existing rows
					populateBasicTable(tables);
				});
			} finally {
				latch.countDown();
			}
		});

		new Thread(() -> {
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				executor.shutdown();
				System.gc();
				isLoading = false;
			}
		}).start();
	}
	
	private List<Table> fetchTables() {
		
	    ApiResponse apiResponse = tableController.list(searchTerm, "id", "asc", currentPage, pageSize, isShowDeleted ? "all" : "without_deleted");
	    if (apiResponse.getErrors() != null) {
	        String errorMessage = apiResponse.getErrors().toString();
	        Toast.show(tableFormUI, Toast.Type.ERROR, apiResponse.getMessage() + ":\n" + errorMessage);
	        return new ArrayList<>();
	    }

	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new JavaTimeModule());
	        
	        // Ép kiểu `data` về Map<String, Object>
	        Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();
	        
	        totalPages = (Integer) responseData.get("total_pages");
	        
	        // Lấy danh sách `data` từ response
	        Object rawData = responseData.get("data");

	        // Chuyển rawData thành JSON string rồi parse thành List<Table>
	        String jsonData = objectMapper.writeValueAsString(rawData);
	        return objectMapper.readValue(jsonData, new TypeReference<List<Table>>() {});
	        
	    } catch (Exception e) {
	    	System.err.println(e.getMessage());
	        Toast.show(tableFormUI, Toast.Type.ERROR, "Failed to parse data: " + e.getMessage());
	        
	        return new ArrayList<>();
	    }
	}

	private void populateCardTable(List<Table> tables) {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		CountDownLatch latch = new CountDownLatch(tables.size());
		List<CardTable> cardTables = new ArrayList<>();
		tables.parallelStream().forEach(modelTable -> {
			executor.submit(() -> {
				try {
					CardTable cardTable = new CardTable(modelTable);
					cardTable.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							showPopup(e);
						}

						private void showPopup(MouseEvent e) {
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
									tableFormUI.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
								}
							}
						}
					});
					synchronized (cardTables) {
						cardTables.add(cardTable);
					}
				} finally {
					latch.countDown();
				}
			});
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			executor.shutdown();
		}
		cardTables.sort(Comparator.comparingLong(cardTable -> cardTable.getModel().getId()));
		SwingUtilities.invokeLater(() -> {
			cardTables.forEach(tableFormUI.getPanelCard()::add);
			tableFormUI.getPanelCard().revalidate();
			tableFormUI.getPanelCard().repaint();
		});
	}

	private void populateBasicTable(List<Table> tables) {
		ExecutorService executor = Executors.newFixedThreadPool(4);
		CountDownLatch latch = new CountDownLatch(tables.size());
		List<Object[]> rows = new ArrayList<>();
		tables.parallelStream().forEach(table -> {
			executor.submit(() -> {
				try {
					Object[] row = tableController.toTableRow(table);
					synchronized (rows) {
						rows.add(row);
					}
				} finally {
					latch.countDown();
				}
			});
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			executor.shutdown();
		}
		Object[][] rowsArray = rows.toArray(new Object[0][]);
		Arrays.parallelSort(rowsArray, (row1, row2) -> Long.compare((Long) row1[1], (Long) row2[1]));
		SwingUtilities.invokeLater(() -> {
			for (Object[] row : rowsArray) {
				tableFormUI.getTableModel().addRow(row);
			}
		});
	}

	public void handleSearchTextChange(JTextField txtSearch) {
		if (debounceTimer != null && debounceTimer.isRunning()) {
			debounceTimer.stop();
		}
		debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> loadData());
		debounceTimer.setRepeats(false);
		debounceTimer.start();
	}

	public void showModal(String userAction) {
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
		if (tableFormUI.getSelectedTitle().equals("Basic table")) {
			if (!validateSingleRowSelection("view details"))
				return;
			idHolder[0] = (long) tableFormUI.getTable().getValueAt(tableFormUI.getTable().getSelectedRow(), 1);
		} else if (tableFormUI.getSelectedTitle().equals("Grid table")) {
			if (!validateSingleCardSelection(idHolder, "view details"))
				return;
		}
		TableInfoForm infoFormTable = createInfoFormTable(idHolder[0]);
		ModalDialog.showModal(tableFormUI, new AdaptSimpleModalBorder(infoFormTable, "Table details information",
				AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
				}), DefaultComponent.getInfoForm());
	}

	private void showCreateModal() {
		CreateTableInputForm inputFormCreateTable = new CreateTableInputForm();
		ModalDialog.showModal(tableFormUI, new AdaptSimpleModalBorder(inputFormCreateTable, "Create table",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleCreateTable(inputFormCreateTable);
					}
				}), DefaultComponent.getInputForm());
	}

	private TableInfoForm createInfoFormTable(long tableId) {
		try {
			return new TableInfoForm(tableId);
		} catch (IOException e) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Failed to find table to view details: " + e.getMessage());
			return null;
		}
	}

	private void handleCreateTable(CreateTableInputForm inputFormCreateTable) {
		tableController.createTable(inputFormCreateTable.getData());
		Toast.show(tableFormUI, Toast.Type.SUCCESS, "Create table successfully");
		loadData();
	}

	private void showEditModal() {
		long[] idHolder = { -1L };
		if (tableFormUI.getSelectedTitle().equals("Basic table")) {
			if (!validateSingleRowSelection("edit"))
				return;
			idHolder[0] = (long) tableFormUI.getTable().getValueAt(tableFormUI.getTable().getSelectedRow(), 1);
		} else if (tableFormUI.getSelectedTitle().equals("Grid table")) {
			if (!validateSingleCardSelection(idHolder, "edit"))
				return;
		}
		Table table = null;
		table = null; // tableController.getTableById(idHolder[0]);
		UpdateTableInputForm inputFormUpdateTable = createInputFormUpdateTable(idHolder[0]);
		ModalDialog.showModal(tableFormUI, new AdaptSimpleModalBorder(inputFormUpdateTable, "Update table",
				AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
					if (action == AdaptSimpleModalBorder.YES_OPTION) {
						handleUpdateTable(inputFormUpdateTable);
					}
				}), DefaultComponent.getInputForm());
	}

	private boolean validateSingleRowSelection(String action) {
		if (tableFormUI.getTable().getSelectedRowCount() > 1) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Please select only one row to " + action);
			return false;
		}
		if (tableFormUI.getTable().getSelectedRowCount() == 0) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Please select a row to " + action);
			return false;
		}
		return true;
	}

	private boolean validateSingleCardSelection(long[] idHolder, String action) {
		List<Long> selectedIds = findSelectedTableIds(tableFormUI.getPanelCard());
		if (selectedIds.size() > 1) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Please select only one row to " + action);
			return false;
		}
		if (selectedIds.isEmpty()) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Please select a row to " + action);
			return false;
		}
		idHolder[0] = selectedIds.get(0);
		return true;
	}

	private UpdateTableInputForm createInputFormUpdateTable(long tableId) {
		try {
			return new UpdateTableInputForm(tableId);
		} catch (IOException e) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Failed to find table to edit: " + e.getMessage());
			return null;
		}
	}

	private void handleUpdateTable(UpdateTableInputForm inputFormUpdateTable) {
		try {
			tableController.updateTable(inputFormUpdateTable.getData());
			Toast.show(tableFormUI, Toast.Type.SUCCESS, "Update table successfully");
			loadData();
		} catch (IOException | BusinessException e) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "Failed to update table: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void showDeleteModal() {
		List<Long> findSelectedTableIds = getSelectedTableIdsForDeletion();
		if (findSelectedTableIds.isEmpty()) {
			Toast.show(tableFormUI, Toast.Type.ERROR, "You have to select at least one table to delete");
			return;
		}
		confirmDeletion(findSelectedTableIds);
	}

	private List<Long> getSelectedTableIdsForDeletion() {
		if (tableFormUI.getSelectedTitle().equals("Basic table")) {
			return findSelectedTableIds();
		} else if (tableFormUI.getSelectedTitle().equals("Grid table")) {
			return findSelectedTableIds(tableFormUI.getPanelCard());
		}
		return new ArrayList<>();
	}

	private void confirmDeletion(List<Long> findSelectedTableIds) {
		if (findSelectedTableIds.size() == 1) {
			confirmSingleDeletion(findSelectedTableIds.get(0));
		} else {
			confirmMultipleDeletion(findSelectedTableIds);
		}
	}

	private void confirmSingleDeletion(Long tableId) {
		ModalDialog.showModal(tableFormUI,
				new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
						"Are you sure you want to delete this table: " + tableId + "? This action cannot be undone.",
						"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								deleteTable(tableId);
							}
						}),
				DefaultComponent.getChoiceModal());
	}

	private void confirmMultipleDeletion(List<Long> findSelectedTableIds) {
		ModalDialog.showModal(tableFormUI,
				new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
						"Are you sure you want to delete these tables: " + findSelectedTableIds
								+ "? This action cannot be undone.",
						"Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
							if (action == AdaptSimpleModalBorder.YES_OPTION) {
								deleteTables(findSelectedTableIds);
							}
						}),
				DefaultComponent.getChoiceModal());
	}

	private void deleteTable(Long tableId) {
		tableController.deleteTable(tableId);
		loadData();
		Toast.show(tableFormUI, Toast.Type.SUCCESS, "Delete table successfully");
	}

	private void deleteTables(List<Long> tableIds) {
		tableController.deleteTables(tableIds);
		loadData();
		Toast.show(tableFormUI, Toast.Type.SUCCESS, "Delete tables successfully");
	}

	public List<Long> findSelectedTableIds() {
		int rowCount = tableFormUI.getTable().getRowCount();
		Set<Long> tableIdsToDelete = Collections.synchronizedSet(new HashSet<>());
		List<List<Long>> chunks = createChunks(rowCount);
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<Callable<Set<Long>>> tasks = new ArrayList<>();

		for (List<Long> chunk : chunks) {
			tasks.add(() -> new HashSet<>(processChunk(chunk)));
		}

		try {
			List<Future<Set<Long>>> results = executorService.invokeAll(tasks);
			for (Future<Set<Long>> result : results) {
				tableIdsToDelete.addAll(result.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
		}
		return new ArrayList<>(tableIdsToDelete);
	}

	private List<List<Long>> createChunks(int rowCount) {
		List<List<Long>> chunks = new ArrayList<>();
		List<Long> currentChunk = new ArrayList<>();

		for (int i = 0; i < rowCount; i++) {
			Long tableId = (Long) tableFormUI.getTable().getValueAt(i, 1);
			currentChunk.add(tableId);

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

	private List<Long> processChunk(List<Long> chunk) {
		List<Long> tableIdsToDelete = new ArrayList<>();

		for (Long tableId : chunk) {
			int rowIndex = findRowIndexById(tableId);
			Boolean isChecked = (Boolean) tableFormUI.getTable().getValueAt(rowIndex, 0);
			if (isChecked != null && isChecked) {
				tableIdsToDelete.add(tableId);
			}
		}

		return tableIdsToDelete;
	}

	private int findRowIndexById(Long tableId) {
		for (int i = 0; i < tableFormUI.getTable().getRowCount(); i++) {
			if (tableFormUI.getTable().getValueAt(i, 1).equals(tableId)) {
				return i;
			}
		}
		return -1;
	}

	public List<Long> findSelectedTableIds(JPanel panelCard) {
		List<Long> selectedTableIds = new ArrayList<>();
		Component[] components = panelCard.getComponents();
		List<List<Component>> chunks = createComponentChunks(components);
		ExecutorService executorService = Executors.newFixedThreadPool(4);
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

	private List<List<Component>> createComponentChunks(Component[] components) {
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

	private List<Long> processComponentChunk(List<Component> chunk) {
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
	
	public void handleSearchButton(JTextField txtSearch, JSpinner spnCurrentPage, JTextField txtTotalPages) {
	    searchTerm = txtSearch.getText();
	    moveToFirst(spnCurrentPage, txtTotalPages);
	}

	public void moveToFirst(JSpinner spnCurrentPage, JTextField txtTotalPages) {
	    currentPage = 1;
	    reloadData();
	    spnCurrentPage.setValue(currentPage);
	    updatePaginationControls(spnCurrentPage, txtTotalPages);
	}

	public void moveToLast(JSpinner spnCurrentPage, JTextField txtTotalPages) {
	    currentPage = getTotalPages();
	    reloadData();
	    spnCurrentPage.setValue(currentPage);
	    updatePaginationControls(spnCurrentPage, txtTotalPages);
	}

	public void checkChkShowDeleted(JCheckBox chkShowDeleted, JSpinner spnCurrentPage, JTextField txtTotalPages) {
	    isShowDeleted = chkShowDeleted.isSelected();
	    moveToFirst(spnCurrentPage, txtTotalPages);
	}

	public void moveToPage(JSpinner spnCurrentPage) {
	    currentPage = (int) spnCurrentPage.getValue();
	    reloadData();
	}

	public void changePageSize(JSpinner spnPageSize, JSpinner spnCurrentPage, JTextField txtTotalPages) {
	    pageSize = (int) spnPageSize.getValue();
	    moveToFirst(spnCurrentPage, txtTotalPages);
	}

	/**
	 * Cập nhật các controls phân trang
	 */
	private void updatePaginationControls(JSpinner spnCurrentPage, JTextField txtTotalPages) {
	    int totalPages = getTotalPages();
	    ((SpinnerNumberModel) spnCurrentPage.getModel()).setMaximum(totalPages);
	    txtTotalPages.setText("/   " + totalPages);
	}

	/**
	 * Load lại dữ liệu và fetchTables
	 */
	public void reloadData() {
	    loadData();
	}

	/**
	 * Lấy tổng số trang, không thay đổi tham số truyền vào
	 */
	public int getTotalPages() {
	    fetchTables();
	    return totalPages;
	}
}