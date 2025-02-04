package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.ControllerTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.TableFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.InputFormCreateTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.InfoFormTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.InputFormUpdateTable;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.system.FormManager;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;

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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FormTableController {
    private TableFormUI formTableUI;
    private ControllerTable controllerTable = new ControllerTable();
    private static final int DEBOUNCE_DELAY = 1000;
    private Timer debounceTimer;

    public FormTableController(TableFormUI formTableUI) {
        this.formTableUI = formTableUI;
    }

    private volatile boolean isLoading = false;

    /**
     * Loads data based on the search term.
     * 
     * @param searchTerm the term to search for tables
     */
    public void loadData(String searchTerm) {
        if (isLoading)
            return;
        isLoading = true;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                formTableUI.getPanelCard().removeAll();
                List<Table> tables = fetchTables(searchTerm);
                if (tables == null || tables.isEmpty()) {
                    Toast.show(formTableUI, Toast.Type.INFO, "No table in database or in search");
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    formTableUI.getPanelCard().removeAll();
                    populateCardTable(tables);
                });
                populateCardTable(tables);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                List<Table> tables = fetchTables(searchTerm);
                SwingUtilities.invokeLater(() -> {
                    formTableUI. getTableModel().setRowCount(0); // Clear existing rows
                    populateBasicTable(tables);
                });
            } catch (IOException e) {
                e.printStackTrace();
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

    /**
     * Fetches tables based on the search term.
     * 
     * @param searchTerm the term to search for tables
     * @return a list of tables matching the search term
     * @throws IOException if an I/O error occurs
     */
    private List<Table> fetchTables(String searchTerm) throws IOException {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return controllerTable.searchTables(searchTerm);
        } else {
            return controllerTable.getAllTables();
        }
    }

    /**
     * Populates the card table with the provided list of tables.
     * 
     * @param tables the list of tables to populate
     */
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
                                    formTableUI.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
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
            cardTables.forEach(formTableUI.getPanelCard()::add);
            formTableUI.getPanelCard().revalidate();
            formTableUI.getPanelCard().repaint();
        });
    }

    /**
     * Populates the basic table with the provided list of tables.
     * 
     * @param tables the list of tables to populate
     */
    private void populateBasicTable(List<Table> tables) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(tables.size());
        List<Object[]> rows = new ArrayList<>();
        tables.parallelStream().forEach(modelTable -> {
            executor.submit(() -> {
                try {
                    Object[] row = modelTable.toTableRowBasic();
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
                formTableUI.getTableModel().addRow(row);
            }
        });
    }

    /**
     * Handles the search text change event with a debounce mechanism.
     * 
     * @param txtSearch the text field containing the search term
     */
    public void handleSearchTextChange(JTextField txtSearch) {
        if (debounceTimer != null && debounceTimer.isRunning()) {
            debounceTimer.stop();
        }
        debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> loadData(txtSearch.getText()));
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }

    /**
     * Shows a modal dialog based on the user action.
     * 
     * @param userAction the action to perform (details, create, edit, delete)
     */
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

    /**
     * Shows the details modal for the selected table.
     */
    private void showDetailsModal() {
        long[] idHolder = { -1L };
        if (formTableUI.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("view details"))
                return;
            idHolder[0] = (long) formTableUI.getTable().getValueAt(formTableUI.getTable().getSelectedRow(), 1);
        } else if (formTableUI.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "view details"))
                return;
        }
        InfoFormTable infoFormTable = createInfoFormTable(idHolder[0]);
        ModalDialog.showModal(formTableUI, new AdaptSimpleModalBorder(infoFormTable, "Table details information",
                AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {
                }), DefaultComponent.getInfoForm());
    }

    /**
     * Shows the create modal for adding a new table.
     */
    private void showCreateModal() {
        InputFormCreateTable inputFormCreateTable = new InputFormCreateTable();
        ModalDialog.showModal(formTableUI, new AdaptSimpleModalBorder(inputFormCreateTable, "Create table",
                AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
                    if (action == AdaptSimpleModalBorder.YES_OPTION) {
                        handleCreateTable(inputFormCreateTable);
                    }
                }), DefaultComponent.getInputForm());
    }

    /**
     * Creates an info form table for the specified table ID.
     * 
     * @param tableId the ID of the table to create the info form for
     * @return the created InfoFormTable
     */
    private InfoFormTable createInfoFormTable(long tableId) {
        try {
            return new InfoFormTable(tableId);
        } catch (IOException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to find table to view details: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handles the creation of a new table.
     * 
     * @param inputFormCreateTable the form containing the new table data
     */
    private void handleCreateTable(InputFormCreateTable inputFormCreateTable) {
        try {
            controllerTable.createTable(inputFormCreateTable.getData());
            Toast.show(formTableUI, Toast.Type.SUCCESS, "Create table successfully");
            loadData("");
        } catch (IOException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to create table: " + e.getMessage());
        }
    }

    /**
     * Shows the edit modal for updating the selected table.
     */
    private void showEditModal() {
        long[] idHolder = { -1L };
        if (formTableUI.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("edit"))
                return;
            idHolder[0] = (long) formTableUI.getTable().getValueAt(formTableUI.getTable().getSelectedRow(), 1);
        } else if (formTableUI.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "edit"))
                return;
        }
        Table table = null;
        try {
            table = controllerTable.getTableById(idHolder[0]);
        } catch (IOException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to find table to edit: " + e.getMessage());
        }
        InputFormUpdateTable inputFormUpdateTable = createInputFormUpdateTable(idHolder[0]);
        ModalDialog.showModal(formTableUI, new AdaptSimpleModalBorder(inputFormUpdateTable, "Update table",
                AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
                    if (action == AdaptSimpleModalBorder.YES_OPTION) {
                        handleUpdateTable(inputFormUpdateTable);
                    }
                }), DefaultComponent.getInputForm());
    }

    /**
     * Validates that only a single row is selected for the specified action.
     * 
     * @param action the action being validated for
     * @return true if validation passes, false otherwise
     */
    private boolean validateSingleRowSelection(String action) {
        if (formTableUI.getTable().getSelectedRowCount() > 1) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (formTableUI.getTable().getSelectedRowCount() == 0) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        return true;
    }

    /**
     * Validates that only a single card is selected for the specified action.
     * 
     * @param idHolder an array to hold the selected ID
     * @param action the action being validated for
     * @return true if validation passes, false otherwise
     */
    private boolean validateSingleCardSelection(long[] idHolder, String action) {
        List<Long> selectedIds = findSelectedTableIds(formTableUI.getPanelCard());
        if (selectedIds.size() > 1) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (selectedIds.isEmpty()) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        idHolder[0] = selectedIds.get(0);
        return true;
    }

    /**
     * Creates an input form update table for the specified table ID.
     * 
     * @param tableId the ID of the table to create the update form for
     * @return the created InputFormUpdateTable
     */
    private InputFormUpdateTable createInputFormUpdateTable(long tableId) {
        try {
            return new InputFormUpdateTable(tableId);
        } catch (IOException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to find table to edit: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handles the update of a table.
     * 
     * @param inputFormUpdateTable the form containing the updated table data
     */
    private void handleUpdateTable(InputFormUpdateTable inputFormUpdateTable) {
        try {
            controllerTable.updateTable(inputFormUpdateTable.getData());
            Toast.show(formTableUI, Toast.Type.SUCCESS, "Update table successfully");
            loadData("");
        } catch (IOException | BusinessException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to update table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows the delete modal for the selected tables.
     */
    private void showDeleteModal() {
        List<Long> findSelectedTableIds = getSelectedTableIdsForDeletion();
        if (findSelectedTableIds.isEmpty() && (formTableUI.getTable().getSelectedRow() == -1)) {
            Toast.show(formTableUI, Toast.Type.ERROR, "You have to select at least one table to delete");
            return;
        }
        confirmDeletion(findSelectedTableIds);
    }

    /**
     * Gets the selected table IDs for deletion.
     * 
     * @return a list of selected table IDs
     */
    private List<Long> getSelectedTableIdsForDeletion() {
        if (formTableUI.getSelectedTitle().equals("Basic table")) {
            return findSelectedTableIds();
        } else if (formTableUI.getSelectedTitle().equals("Grid table")) {
            return findSelectedTableIds(formTableUI.getPanelCard());
        }
        return new ArrayList<>();
    }

    /**
     * Confirms the deletion of the specified table IDs.
     * 
     * @param findSelectedTableIds the list of table IDs to confirm deletion for
     */
    private void confirmDeletion(List<Long> findSelectedTableIds) {
        if (findSelectedTableIds.size() == 1) {
            confirmSingleDeletion(findSelectedTableIds.get(0));
        } else {
            confirmMultipleDeletion(findSelectedTableIds);
        }
    }

    /**
     * Confirms the deletion of a single table.
     * 
     * @param tableId the ID of the table to delete
     */
    private void confirmSingleDeletion(Long tableId) {
        ModalDialog.showModal(formTableUI,
                new SimpleMessageModal(SimpleMessageModal.Type.WARNING,
                        "Are you sure you want to delete this table: " + tableId + "? This action cannot be undone.",
                        "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
                            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                                deleteTable(tableId);
                            }
                        }),
                DefaultComponent.getChoiceModal());
    }

    /**
     * Confirms the deletion of multiple tables.
     * 
     * @param findSelectedTableIds the list of table IDs to delete
     */
    private void confirmMultipleDeletion(List<Long> findSelectedTableIds) {
        ModalDialog.showModal(formTableUI,
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

    /**
     * Deletes a table with the specified ID.
     * 
     * @param tableId the ID of the table to delete
     */
    private void deleteTable(Long tableId) {
        try {
            controllerTable.deleteTable(tableId);
            formTableUI.refreshTable();
            Toast.show(formTableUI, Toast.Type.SUCCESS, "Delete table successfully");
        } catch (IOException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to delete table: " + e.getMessage());
        }
    }

    /**
     * Deletes multiple tables with the specified IDs.
     * 
     * @param tableIds the list of table IDs to delete
     */
    private void deleteTables(List<Long> tableIds) {
        try {
            controllerTable.deleteTables(tableIds);
            formTableUI.refreshTable();
            Toast.show(formTableUI, Toast.Type.SUCCESS, "Delete tables successfully");
        } catch (IOException e) {
            Toast.show(formTableUI, Toast.Type.ERROR, "Failed to delete tables: " + e.getMessage());
        }
    }

    /**
     * Finds the selected table IDs from the basic table.
     * 
     * @return a list of selected table IDs
     */
    public List<Long> findSelectedTableIds() {
        int rowCount = formTableUI.getTable().getRowCount();
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

    /**
     * Creates chunks of rows for processing.
     * 
     * @param rowCount the total number of rows
     * @return a list of chunks containing row IDs
     */
    private List<List<Long>> createChunks(int rowCount) {
        List<List<Long>> chunks = new ArrayList<>();
        List<Long> currentChunk = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Long tableId = (Long) formTableUI.getTable().getValueAt(i, 1);
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

    /**
     * Processes a chunk of rows to find selected table IDs.
     * 
     * @param chunk the chunk of row IDs to process
     * @return a list of selected table IDs
     */
    private List<Long> processChunk(List<Long> chunk) {
        List<Long> tableIdsToDelete = new ArrayList<>();

        for (Long tableId : chunk) {
            int rowIndex = findRowIndexById(tableId);
            Boolean isChecked = (Boolean) formTableUI.getTable().getValueAt(rowIndex, 0);
            if (isChecked != null && isChecked) {
                tableIdsToDelete.add(tableId);
            }
        }

        return tableIdsToDelete;
    }

    /**
     * Finds the row index by the specified table ID.
     * 
     * @param tableId the ID of the table to find
     * @return the index of the row, or -1 if not found
     */
    private int findRowIndexById(Long tableId) {
        for (int i = 0; i < formTableUI.getTable().getRowCount(); i++) {
            if (formTableUI.getTable().getValueAt(i, 1).equals(tableId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the selected table IDs from the specified panel.
     * 
     * @param panelCard the panel containing card tables
     * @return a list of selected table IDs
     */
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

    /**
     * Creates chunks of components for processing.
     * 
     * @param components the array of components to chunk
     * @return a list of chunks containing components
     */
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

    /**
     * Processes a chunk of components to find selected table IDs.
     * 
     * @param chunk the chunk of components to process
     * @return a list of selected table IDs
     */
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
}