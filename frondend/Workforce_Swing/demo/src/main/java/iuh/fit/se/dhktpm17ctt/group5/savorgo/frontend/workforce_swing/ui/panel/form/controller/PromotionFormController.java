package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.PromotionController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.PromotionStatusEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Promotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardPromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.PromotionFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreatePromotionInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.PromotionInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.UpdatePromotionInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PromotionFormController {
    private PromotionFormUI promotionFormUI;
    private static final int DEBOUNCE_DELAY = 1000; // Debounce delay in milliseconds
    private Timer debounceTimer;
    private PromotionController promotionController = new PromotionController();
    private MenuController controllerMenu = new MenuController();
    private int currentPage = 1;
	private boolean isShowDeleted = false;
	private Integer totalPages = Integer.MAX_VALUE;
	private int pageSize;
	private String searchTerm = "";
	
    public PromotionFormController(PromotionFormUI formPromotion) {
        this.promotionFormUI = formPromotion;
    }

    private volatile boolean isLoading = false;

    public void loadData() {
        if (isLoading)
            return;
        isLoading = true;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                List<Promotion> promotions = fetchPromotions();
                if (promotions == null || promotions.isEmpty()) {
                    Toast.show(promotionFormUI, Toast.Type.INFO, "No promotion in database or in search");
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    promotionFormUI.getPanelCard().removeAll();
                    populateCardTable(promotions);
                });
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                List<Promotion> promotions = fetchPromotions();
                SwingUtilities.invokeLater(() -> {
                    promotionFormUI.getTableModel().setRowCount(0); // Clear existing rows
                    populateBasicTable(promotions);
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

    private List<Promotion> fetchPromotions() {
        try {
            ApiResponse apiResponse = promotionController.list(searchTerm, "id", "asc", currentPage, pageSize,
                    isShowDeleted ? "all" : "without_deleted");

            if (apiResponse.getErrors() != null) {
                String errorMessage = apiResponse.getErrors().toString();
                System.out.println(promotionFormUI + " + and + " + errorMessage);
                //Toast.show(promotionFormUI, Toast.Type.ERROR, apiResponse.getMessage() + ":\n" + errorMessage);
                return new ArrayList<>();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Ép kiểu data về Map<String, Object>
            Map<String, Object> responseData = (Map<String, Object>) apiResponse.getData();
            totalPages = (Integer) responseData.get("total_pages");

            // Lấy danh sách data từ response
            Object rawData = responseData.get("data");

            // Chuyển rawData thành JSON string rồi parse thành List<Promotion>
            String jsonData = objectMapper.writeValueAsString(rawData);
            return objectMapper.readValue(jsonData, new TypeReference<List<Promotion>>() {
            });

        } catch (Exception e) {
           	e.printStackTrace();
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to parse data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void populateBasicTable(List<Promotion> promotions) {
        // Xóa tất cả hàng trong JTable trước khi thêm mới
        SwingUtilities.invokeLater(() -> promotionFormUI.getTableModel().setRowCount(0));

        promotions.parallelStream().forEachOrdered(promotion -> {
            System.out.println("Đang load table: " + promotion.getName());

            try {
                final Object[] rowData = promotionController.getTableRow(promotion);

                // Chỉ cập nhật UI nếu lấy được dữ liệu hợp lệ
                if (rowData != null) {
                    SwingUtilities.invokeLater(() -> promotionFormUI.getTableModel().addRow(rowData));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> 
                    Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to find menu relation with promotion")
                );
                e.printStackTrace();
            }
        });

        // Cập nhật giao diện sau khi thêm tất cả promotion vào bảng
        SwingUtilities.invokeLater(() -> promotionFormUI.getTableModel().fireTableDataChanged());
    }

    private void populateCardTable(List<Promotion> promotions) {
        // Xóa hết các card trước khi thêm mới
        SwingUtilities.invokeLater(() -> promotionFormUI.getPanelCard().removeAll());

        promotions.parallelStream().forEachOrdered(promotion -> {
            CardPromotion promotionCard = new CardPromotion(promotion);
            System.out.println("Đang load card: " + promotion.getName());

            // Thêm MouseListener cho cardPromotion
            promotionCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
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
                            promotionFormUI.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            });

            // Cập nhật UI phải chạy trên EDT
            SwingUtilities.invokeLater(() -> promotionFormUI.getPanelCard().add(promotionCard));
        });

        // Cập nhật giao diện sau khi thêm tất cả CardPromotion
        SwingUtilities.invokeLater(() -> {
            promotionFormUI.getPanelCard().revalidate();
            promotionFormUI.getPanelCard().repaint();
        });
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
        if (promotionFormUI.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("view details"))
                return;
            idHolder[0] = (long) promotionFormUI.getTable().getValueAt(promotionFormUI.getTable().getSelectedRow(), 1);
        } else if (promotionFormUI.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "view details"))
                return;
        }
        PromotionInfoForm infoFormPromotion = createInfoFormPromotion(idHolder[0]);
        ModalDialog.showModal(promotionFormUI, new AdaptSimpleModalBorder(infoFormPromotion, "Promotion details information", AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {}), DefaultComponent.getInfoForm());
    }

    private void showCreateModal() {
        CreatePromotionInputForm inputFormCreatePromotion = new CreatePromotionInputForm(null);
        ModalDialog.showModal(promotionFormUI, new AdaptSimpleModalBorder(inputFormCreatePromotion, "Create promotions", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleCreatePromotion(inputFormCreatePromotion);
            }
        }), DefaultComponent.getInputFormDoubleSize());
    }

    private void handleCreatePromotion(CreatePromotionInputForm inputFormCreatePromotion) {
	    ApiResponse response = promotionController.createPromotions(inputFormCreatePromotion.getData());

	    if (response.getStatus() == 201 && response.getData() != null) {
	    	Toast.show(promotionFormUI, Toast.Type.SUCCESS, "Promotion created successfully");
	    	reloadData();
	    } else {
	        String errorMessage = response.getErrors() != null ? response.getErrors().toString() : "Unknown error";
	        Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to create promotion: " + errorMessage);
	    }
	}


    private void showEditModal() {
        long[] idHolder = { -1L };
        
        if (promotionFormUI.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("edit"))
                return;
            idHolder[0] = (long) promotionFormUI.getTable().getValueAt(promotionFormUI.getTable().getSelectedRow(), 1);
        } else if (promotionFormUI.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "edit"))
                return;
        }
        
        ApiResponse apiResponse = promotionController.getPromotionById(idHolder[0]);

        if (apiResponse.getErrors() != null || apiResponse.getData() == null) {
            String errorMessage = apiResponse.getErrors() != null ? apiResponse.getErrors().toString() : "Unknown error";
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to fetch promotion: " + errorMessage);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Promotion promotion;
        try {
            String jsonData = objectMapper.writeValueAsString(apiResponse.getData());
            promotion = objectMapper.readValue(jsonData, Promotion.class);
        } catch (IOException e) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to parse data: " + e.getMessage());
            return;
        }

        if (promotion.getStatus().equals(PromotionStatusEnum.DELETED)) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Cannot edit deleted promotion");
            return;
        }

        UpdatePromotionInputForm updateFormUpdatePromotion = createInputFormUpdatePromotion(promotion.getId());
        ModalDialog.showModal(promotionFormUI, new AdaptSimpleModalBorder(updateFormUpdatePromotion, "Update promotion",
                AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
                    if (action == AdaptSimpleModalBorder.YES_OPTION) {
                        handleUpdatePromotion(updateFormUpdatePromotion);
                    }
                }), DefaultComponent.getInputForm());
    }


    private boolean validateSingleRowSelection(String action) {
        if (promotionFormUI.getTable().getSelectedRowCount() > 1) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (promotionFormUI.getTable().getSelectedRowCount() == 0) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        return true;
    }

    private boolean validateSingleCardSelection(long[] idHolder, String action) {
        List<Long> selectedIds = findSelectedPromotionIds(promotionFormUI.getPanelCard());
        if (selectedIds.size() > 1) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (selectedIds.isEmpty()) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        idHolder[0] = selectedIds.get(0);
        return true;
    }

    private UpdatePromotionInputForm createInputFormUpdatePromotion(long promotionId) {
        ApiResponse promotionResponse = promotionController.getPromotionById(promotionId);

        if (promotionResponse.getErrors() != null || promotionResponse.getData() == null) {
            String errorMessage = promotionResponse.getErrors() != null ? promotionResponse.getErrors().toString() : "Unknown error";
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to fetch promotion: " + errorMessage);
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Promotion promotion;
        try {
            String jsonData = objectMapper.writeValueAsString(promotionResponse.getData());
            promotion = objectMapper.readValue(jsonData, Promotion.class);
        } catch (IOException e) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to parse promotion data: " + e.getMessage());
            return null;
        }

        ApiResponse menuResponse = controllerMenu.getMenuById(promotion.getMenuId());

        if (menuResponse.getErrors() != null || menuResponse.getData() == null) {
            String errorMessage = menuResponse.getErrors() != null ? menuResponse.getErrors().toString() : "Unknown error";
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to fetch menu: " + errorMessage);
            return null;
        }

        Menu menu;
        try {
            String jsonData = objectMapper.writeValueAsString(menuResponse.getData());
            menu = objectMapper.readValue(jsonData, Menu.class);
        } catch (IOException e) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to parse menu data: " + e.getMessage());
            return null;
        }

        return new UpdatePromotionInputForm(promotion, menu);
    }

    private PromotionInfoForm createInfoFormPromotion(long promotionId) {
        try {
            return new PromotionInfoForm(promotionId);
        } catch (IOException e) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to find promotion to view details: " + e.getMessage());
            return null;
        }
    }

    private void handleUpdatePromotion(UpdatePromotionInputForm inputFormUpdatePromotion) {
        Object[] promotionData = inputFormUpdatePromotion.getData();
        ApiResponse response = promotionController.updatePromotion(promotionData);

        if (response.getStatus() == 200 && response.getData() != null) {
            Toast.show(promotionFormUI, Toast.Type.SUCCESS, "Promotion updated successfully");
            promotionFormUI.formRefresh();
        } else {
            String errorMessage = response.getErrors() != null ? response.getErrors().toString() : "Unknown error";
            Toast.show(promotionFormUI, Toast.Type.ERROR, "Failed to update promotion: " + errorMessage);
        }
    }

    private void showDeleteModal() {
        List<Long> findSelectedPromotionIds = findSelectedPromotionIdsForDeletion();
        if (findSelectedPromotionIds.isEmpty()) {
            Toast.show(promotionFormUI, Toast.Type.ERROR, "You have to select at least one promotion to delete");
            return;
        }
        confirmDeletion(findSelectedPromotionIds);
    }

    private List<Long> findSelectedPromotionIdsForDeletion() {
        if (promotionFormUI.getSelectedTitle().equals("Basic table")) {
            return findSelectedPromotionIds();
        } else if (promotionFormUI.getSelectedTitle().equals("Grid table")) {
            return findSelectedPromotionIds(promotionFormUI.getPanelCard());
        }
        return new ArrayList<>();
    }

    private void confirmDeletion(List<Long> findSelectedPromotionIds) {
        if (findSelectedPromotionIds.size() == 1) {
            confirmSingleDeletion(findSelectedPromotionIds.get(0));
        } else {
            confirmMultipleDeletion(findSelectedPromotionIds);
        }
    }

    private void confirmSingleDeletion(Long promotionId) {
        ModalDialog.showModal(promotionFormUI, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete this promotion: " + promotionId + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deletePromotion(promotionId);
            }
        }), DefaultComponent.getChoiceModal());
    }

    private void confirmMultipleDeletion(List<Long> findSelectedPromotionIds) {
        ModalDialog.showModal(promotionFormUI, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete these promotions: " + findSelectedPromotionIds + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deletePromotions(findSelectedPromotionIds);
            }
        }), DefaultComponent.getChoiceModal());
    }

    private void deletePromotion(Long promotionId) {
        promotionController.deletePromotion(promotionId);
		promotionFormUI.formRefresh();
		Toast.show(promotionFormUI, Toast.Type.SUCCESS, "Delete promotion successfully");
    }

    private void deletePromotions(List<Long> findSelectedPromotionIds) {
        promotionController.deletePromotions(findSelectedPromotionIds);
		promotionFormUI.formRefresh();
		Toast.show(promotionFormUI, Toast.Type.SUCCESS, "Delete promotions successfully");
    }

    private static final int CHUNK_SIZE = 4;

    public List<Long> findSelectedPromotionIds() {
        int rowCount = promotionFormUI.getTable().getRowCount();
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

    private List<List<Long>> createChunks(int rowCount) {
        List<List<Long>> chunks = new ArrayList<>();
        List<Long> currentChunk = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Long promotionId = (Long) promotionFormUI.getTable().getValueAt(i, 1);
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

    private List<Long> processChunk(List<Long> chunk) {
        List<Long> promotionIdsToDelete = new ArrayList<>();

        for (Long promotionId : chunk) {
            int rowIndex = findRowIndexById(promotionId);
            Boolean isChecked = (Boolean) promotionFormUI.getTable().getValueAt(rowIndex, 0);
            if (isChecked != null && isChecked) {
                promotionIdsToDelete.add(promotionId);
            }
        }

        return promotionIdsToDelete;
    }

    private int findRowIndexById(Long promotionId) {
        for (int i = 0; i < promotionFormUI.getTable().getRowCount(); i++) {
            if (promotionFormUI.getTable().getValueAt(i, 1).equals(promotionId)) {
                return i;
            }
        }
        return -1;
    }

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

	private void updatePaginationControls(JSpinner spnCurrentPage, JTextField txtTotalPages) {
		int totalPages = getTotalPages();
		((SpinnerNumberModel) spnCurrentPage.getModel()).setMaximum(totalPages);
		txtTotalPages.setText("/   " + totalPages);
	}

	public void reloadData() {
		SwingUtilities.invokeLater(() -> loadData());
	}

	public int getTotalPages() {
		fetchPromotions();
		return totalPages;
	}
}