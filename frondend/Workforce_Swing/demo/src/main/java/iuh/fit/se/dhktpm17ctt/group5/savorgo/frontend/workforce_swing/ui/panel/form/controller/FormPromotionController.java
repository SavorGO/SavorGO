package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.PromotionController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.StatusPromotionEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardPromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.PromotionFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.InputFormCreatePromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.InfoFormPromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.InputFormUpdatePromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
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

public class FormPromotionController {
    private PromotionFormUI formPromotion;
    private static final int DEBOUNCE_DELAY = 1000; // Debounce delay in milliseconds
    private Timer debounceTimer;
    private PromotionController promotionController = new PromotionController();
    private MenuController controllerMenu = new MenuController();

    /**
     * Constructs a FormPromotionController with the specified PromotionFormUI.
     * 
     * @param formPromotion the PromotionFormUI to control
     */
    public FormPromotionController(PromotionFormUI formPromotion) {
        this.formPromotion = formPromotion;
    }

    private volatile boolean isLoading = false;

    /**
     * Loads promotion data based on the provided search term.
     * 
     * @param searchTerm the term to search for in the promotions
     */
    public void loadData(String searchTerm) {
        if (isLoading)
            return;
        isLoading = true;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                List<Promotion> promotions = fetchPromotions(searchTerm);
                if (promotions == null || promotions.isEmpty()) {
                    Toast.show(formPromotion, Toast.Type.INFO, "No promotion in database or in search");
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    formPromotion.getPanelCard().removeAll();
                    try {
						populateCardTable(promotions);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                List<Promotion> promotions = fetchPromotions(searchTerm);
                SwingUtilities.invokeLater(() -> {
                    formPromotion.getTableModel().setRowCount(0); // Clear existing rows
                    populateBasicTable(promotions);
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
     * Fetches the list of promotions based on the search term.
     * 
     * @param searchTerm the term to search for in the promotions
     * @return the list of Promotion objects
     * @throws IOException if an I/O error occurs
     */
    private List<Promotion> fetchPromotions(String searchTerm) throws IOException {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return promotionController.searchPromotions(searchTerm);
        } else {
            return promotionController.getAllPromotions();
        }
    }

    /**
     * Populates the card table with the fetched Promotion objects.
     * 
     * @param promotions the list of Promotion objects to populate
     * @throws IOException if an I/O error occurs
     */
    private void populateCardTable(List<Promotion> promotions) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(promotions.size());
        List<CardPromotion> cardPromotions = new ArrayList<>();

        promotions.parallelStream().forEach(promotion -> {
            executor.submit(() -> {
                try {
                    Menu menu = controllerMenu.getMenuById(promotion.getMenuId());
                    CardPromotion promotionCard = new CardPromotion(promotion);
                    promotionCard.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            showPopup(e);
                        }

                        private void showPopup(MouseEvent e) {
                            if (e.getComponent() instanceof CardPromotion) {
                                if (SwingUtilities.isLeftMouseButton(e)) {
                                    promotionCard.setSelected(!promotionCard.isSelected());
                                    if (promotionCard.isSelected()) {
                                        promotionCard.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
                                    } else {
                                        promotionCard.setBorder(BorderFactory.createEmptyBorder());
                                    }
                                } else if (e.isPopupTrigger()) {
                                    promotionCard.setSelected(true);
                                    promotionCard.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
                                    formPromotion.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                                }
                            }
                        }
                    });
                    synchronized (cardPromotions) {
                        cardPromotions.add(promotionCard);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

        cardPromotions.sort(Comparator.comparingLong(promotionCard -> promotionCard.getModel().getId()));

        SwingUtilities.invokeLater(() -> {
            cardPromotions.forEach(formPromotion.getPanelCard()::add);
            formPromotion.getPanelCard().revalidate();
            formPromotion.getPanelCard().repaint();
        });
    }

    /**
     * Populates the basic table with the fetched Promotion objects.
     * 
     * @param promotions the list of Promotion objects to populate
     */
    private void populateBasicTable(List<Promotion> promotions) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(promotions.size());
        List<Object[]> rows = new ArrayList<>();

        promotions.parallelStream().forEach(promotion -> {
            executor.submit(() -> {
                try {
                    Object[] row = promotionController.getTableRow(promotion);
                    synchronized (rows) {
                        rows.add(row);
                    }
                } catch (IOException e) {
                    Toast.show(formPromotion, Toast.Type.ERROR, "Failed to find menu relation with promotion");
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
                formPromotion.getTableModel().addRow(row);
            }
        });
    }

    /**
     * Handles the search text change with debounce functionality.
     * 
     * @param txtSearch the JTextField containing the search text
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
     * Shows the details modal for the selected promotion.
     */
    private void showDetailsModal() {
        long[] idHolder = { -1L };
        if (formPromotion.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("view details"))
                return;
            idHolder[0] = (long) formPromotion.getTable().getValueAt(formPromotion.getTable().getSelectedRow(), 1);
        } else if (formPromotion.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "view details"))
                return;
        }
        InfoFormPromotion infoFormPromotion = createInfoFormPromotion(idHolder[0]);
        ModalDialog.showModal(formPromotion, new AdaptSimpleModalBorder(infoFormPromotion, "Promotion details information", AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {}), DefaultComponent.getInfoForm());
    }

    /**
     * Shows the create modal for adding a new promotion.
     */
    private void showCreateModal() {
        InputFormCreatePromotion inputFormCreatePromotion = new InputFormCreatePromotion(null);
        ModalDialog.showModal(formPromotion, new AdaptSimpleModalBorder(inputFormCreatePromotion, "Create promotions", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleCreatePromotion(inputFormCreatePromotion);
            }
        }), DefaultComponent.getInputFormDoubleSize());
    }

    /**
     * Handles the creation of a new promotion.
     * 
     * @param inputFormCreatePromotion the form containing the promotion data
     */
    private void handleCreatePromotion(InputFormCreatePromotion inputFormCreatePromotion) {
        try {
            promotionController.createPromotions(inputFormCreatePromotion.getData());
            Toast.show(formPromotion, Toast.Type.SUCCESS, "Create promotion successfully");
            loadData(""); // Reload table data after successful creation
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to create promotion: " + e.getMessage());
        }
    }

    /**
     * Shows the edit modal for updating the selected promotion.
     */
    private void showEditModal() {
        long[] idHolder = { -1L };
        if (formPromotion.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("edit"))
                return;
            idHolder[0] = (long) formPromotion.getTable().getValueAt(formPromotion.getTable().getSelectedRow(), 1);
        } else if (formPromotion.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "edit"))
                return;
        }
        Promotion promotion = null;
        try {
            promotion = promotionController.getPromotionById(idHolder[0]);
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to find promotion to edit: " + e.getMessage());
            return;
        }

        Menu menu = null;
        try {
            menu = controllerMenu.getMenuById(promotion.getMenuId());
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
            return;
        }

        if (promotion.getStatus().equals(StatusPromotionEnum.DELETED)) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Cannot edit deleted promotion");
            return;
        }

        InputFormUpdatePromotion inputFormUpdatePromotion = createInputFormUpdatePromotion(promotion.getId());
        ModalDialog.showModal(formPromotion, new AdaptSimpleModalBorder(inputFormUpdatePromotion, "Update promotion", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleUpdatePromotion(inputFormUpdatePromotion);
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
        if (formPromotion.getTable().getSelectedRowCount() > 1) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (formPromotion.getTable().getSelectedRowCount() == 0) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Please select a row to " + action);
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
        List<Long> selectedIds = findSelectedPromotionIds(formPromotion.getPanelCard());
        if (selectedIds.size() > 1) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (selectedIds.isEmpty()) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        idHolder[0] = selectedIds.get(0);
        return true;
    }

    /**
     * Creates an input form update promotion for the specified promotion.
     * 
     * @param promotionId the ID of the promotion to create the update form for
     * @return the created InputFormUpdatePromotion
     */
    private InputFormUpdatePromotion createInputFormUpdatePromotion(long promotionId) {
        Promotion promotion;
        Menu menu;
        try {
            promotion = promotionController.getPromotionById(promotionId);
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to find promotion to edit: " + e.getMessage());
            return null;
        }
        try {
            menu = controllerMenu.getMenuById(promotion.getMenuId());
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
            return null;
        }
        return new InputFormUpdatePromotion(promotion, menu);
    }

    /**
     * Creates an info form promotion for the specified promotion ID.
     * 
     * @param promotionId the ID of the promotion to create the info form for
     * @return the created InfoFormPromotion
     */
    private InfoFormPromotion createInfoFormPromotion(long promotionId) {
        try {
            return new InfoFormPromotion(promotionId);
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to find promotion to view details: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handles the update of a promotion.
     * 
     * @param inputFormUpdatePromotion the form containing the updated promotion data
     */
    private void handleUpdatePromotion(InputFormUpdatePromotion inputFormUpdatePromotion) {
        Object[] promotionData = inputFormUpdatePromotion.getData();
        try {
            promotionController.updatePromotion(promotionData);
            Toast.show(formPromotion, Toast.Type.SUCCESS, "Update promotion successfully");
            formPromotion.formRefresh();
        } catch (IOException | BusinessException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to update promotion: " + e.getMessage());
        }
    }

    /**
     * Shows the delete modal for the selected promotions.
     */
    private void showDeleteModal() {
        List<Long> findSelectedPromotionIds = getSelectedPromotionIdsForDeletion();
        if (findSelectedPromotionIds.isEmpty() && (formPromotion.getTable().getSelectedRow() == -1)) {
            Toast.show(formPromotion, Toast.Type.ERROR, "You have to select at least one promotion to delete");
            return;
        }
        confirmDeletion(findSelectedPromotionIds);
    }

    /**
     * Gets the selected promotion IDs for deletion.
     * 
     * @return a list of selected promotion IDs
     */
    private List<Long> getSelectedPromotionIdsForDeletion() {
        if (formPromotion.getSelectedTitle().equals("Basic table")) {
            return findSelectedPromotionIds();
        } else if (formPromotion.getSelectedTitle().equals("Grid table")) {
            return findSelectedPromotionIds(formPromotion.getPanelCard());
        }
        return new ArrayList<>();
    }

    /**
     * Confirms the deletion of the specified promotion IDs.
     * 
     * @param findSelectedPromotionIds the list of promotion IDs to confirm deletion for
     */
    private void confirmDeletion(List<Long> findSelectedPromotionIds) {
        if (findSelectedPromotionIds.size() == 1) {
            confirmSingleDeletion(findSelectedPromotionIds.get(0));
        } else {
            confirmMultipleDeletion(findSelectedPromotionIds);
        }
    }

    /**
     * Confirms the deletion of a single promotion.
     * 
     * @param promotionId the ID of the promotion to delete
     */
    private void confirmSingleDeletion(Long promotionId) {
        ModalDialog.showModal(formPromotion, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete this promotion: " + promotionId + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deletePromotion(promotionId);
            }
        }), DefaultComponent.getChoiceModal());
    }

    /**
     * Confirms the deletion of multiple promotions.
     * 
     * @param findSelectedPromotionIds the list of promotion IDs to delete
     */
    private void confirmMultipleDeletion(List<Long> findSelectedPromotionIds) {
        ModalDialog.showModal(formPromotion, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete these promotions: " + findSelectedPromotionIds + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deletePromotions(findSelectedPromotionIds);
            }
        }), DefaultComponent.getChoiceModal());
    }

    /**
     * Deletes a promotion with the specified ID.
     * 
     * @param promotionId the ID of the promotion to delete
     */
    private void deletePromotion(Long promotionId) {
        try {
            promotionController.deletePromotion(promotionId);
            formPromotion.formRefresh();
            Toast.show(formPromotion, Toast.Type.SUCCESS, "Delete promotion successfully");
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to delete promotion: " + e.getMessage());
        }
    }

    /**
     * Deletes multiple promotions with the specified IDs.
     * 
     * @param findSelectedPromotionIds the list of promotion IDs to delete
     */
    private void deletePromotions(List<Long> findSelectedPromotionIds) {
        try {
            promotionController.deletePromotions(findSelectedPromotionIds);
            formPromotion.formRefresh();
            Toast.show(formPromotion, Toast.Type.SUCCESS, "Delete promotions successfully");
        } catch (IOException e) {
            Toast.show(formPromotion, Toast.Type.ERROR, "Failed to delete promotions: " + e.getMessage());
        }
    }

    private static final int CHUNK_SIZE = 4;

    /**
     * Finds the selected promotion IDs from the basic table.
     * 
     * @return a list of selected promotion IDs
     */
    public List<Long> findSelectedPromotionIds() {
        int rowCount = formPromotion.getTable().getRowCount();
        Set<Long> promotionIdsToDelete = Collections.synchronizedSet(new HashSet<>());
        List<List<Long>> chunks = createChunks(rowCount);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Callable<Set<Long>>> tasks = new ArrayList<>();

        for (List<Long> chunk : chunks) {
            tasks.add(() -> new HashSet<>(processChunk(chunk)));
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
     * Creates chunks of rows for processing.
     * 
     * @param rowCount the total number of rows
     * @return a list of chunks containing row IDs
     */
    private List<List<Long>> createChunks(int rowCount) {
        List<List<Long>> chunks = new ArrayList<>();
        List<Long> currentChunk = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Long promotionId = (Long) formPromotion.getTable().getValueAt(i, 1);
            currentChunk.add(promotionId);

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
     * Processes a chunk of rows to find selected promotion IDs.
     * 
     * @param chunk the chunk of row IDs to process
     * @return a list of selected promotion IDs
     */
    private List<Long> processChunk(List<Long> chunk) {
        List<Long> promotionIdsToDelete = new ArrayList<>();

        for (Long promotionId : chunk) {
            int rowIndex = findRowIndexById(promotionId);
            Boolean isChecked = (Boolean) formPromotion.getTable().getValueAt(rowIndex, 0);
            if (isChecked != null && isChecked) {
                promotionIdsToDelete.add(promotionId);
            }
        }

        return promotionIdsToDelete;
    }

    /**
     * Finds the row index by the specified promotion ID.
     * 
     * @param promotionId the ID of the promotion to find
     * @return the index of the row, or -1 if not found
     */
    private int findRowIndexById(Long promotionId) {
        for (int i = 0; i < formPromotion.getTable().getRowCount(); i++) {
            if (formPromotion.getTable().getValueAt(i, 1).equals(promotionId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the selected promotion IDs from the specified panel.
     * 
     * @param panelCard the panel containing card promotions
     * @return a list of selected promotion IDs
     */
    public List<Long> findSelectedPromotionIds(JPanel panelCard) {
        List<Long> selectedPromotionIds = new ArrayList<>();
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
                selectedPromotionIds.addAll(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return selectedPromotionIds;
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
     * Processes a chunk of components to find selected promotion IDs.
     * 
     * @param chunk the chunk of components to process
     * @return a list of selected promotion IDs
     */
    private List<Long> processComponentChunk(List<Component> chunk) {
        List<Long> selectedIds = new ArrayList<>();

        for (Component component : chunk) {
            if (component instanceof CardPromotion) {
                CardPromotion cardPromotion = (CardPromotion) component;
                if (cardPromotion.isSelected()) {
 selectedIds.add(cardPromotion.getModel().getId());
                }
            }
        }

        return selectedIds;
    }
}