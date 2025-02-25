package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.MenuController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.PromotionController;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Table;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.MenuFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.PromotionFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreateMenuInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.CreatePromotionInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.MenuInfoForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.UpdateMenuInputForm;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.ApiResponse;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MenuFormController {
    private MenuFormUI menFormUI;
    private MenuController menuController = new MenuController();
    private PromotionController promotionController = new PromotionController();
    private static final int DEBOUNCE_DELAY = 1000;
    private Timer debounceTimer;
    private volatile boolean isLoading = false;
	private int currentPage = 1;
	private boolean isShowDeleted = false;
	private Integer totalPages = Integer.MAX_VALUE;
	private int pageSize;
	private String searchTerm = "";

    /** 
     * Constructs a FormMenuController with the specified MenuFormUI.
     * 
     * @param formMenu The MenuFormUI instance to control.
     */
    public MenuFormController(MenuFormUI formMenu) {
        this.menFormUI = formMenu;
    }

    /** 
     * Loads data based on the search term.
     * 
     * @param searchTerm the term to search for menus.
     */
    public void loadData() {
        if (isLoading) {
            return;
        }
        isLoading = true;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                List<Menu> menus = fetchMenus();
                if (menus == null || menus.isEmpty()) {
                    Toast.show(menFormUI, Toast.Type.INFO, "No menu in database or in search");
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    menFormUI.getPanelCard().removeAll();
                    populateCardMenu(menus);
                });
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                List<Menu> menus = fetchMenus();
                SwingUtilities.invokeLater(() -> {
                    menFormUI.getTableModel().setRowCount(0);
                    populateBasicMenu(menus);
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
                SwingUtilities.invokeLater(() -> {
                    executor.shutdown();
                    System.gc();
                    isLoading = false;
                });
            }
        }).start();
    }

   
    private List<Menu> fetchMenus() {
	    ApiResponse apiResponse = menuController.list(searchTerm, "id", "asc", currentPage, pageSize, isShowDeleted ? "all" : "without_deleted");
	    if (apiResponse.getErrors() != null) {
	        String errorMessage = apiResponse.getErrors().toString();
	        Toast.show(menFormUI, Toast.Type.ERROR, apiResponse.getMessage() + ":\n" + errorMessage);
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
	        return objectMapper.readValue(jsonData, new TypeReference<List<Menu>>() {});
	        
	    } catch (Exception e) {
	    	System.err.println(e.getMessage());
	        Toast.show(menFormUI, Toast.Type.ERROR, "Failed to parse data: " + e.getMessage());
	        
	        return new ArrayList<>();
	    }
	}

    private void populateCardMenu(List<Menu> menus) {
        SwingWorker<Void, CardMenu> cardWorker = new SwingWorker<Void, CardMenu>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<List<Menu>> groupedMenus = sortAndGroupMenus(menus);
                for (List<Menu> group : groupedMenus) {
                    for (Menu menu : group) {
                        CardMenu cardMenu = new CardMenu(menu);
                        System.out.println("Đang load card: " + menu.getName());
                        
                        // Thêm MouseListener cho cardMenu
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
                                        menFormUI.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                                    }
                                }
                            }
                        });

                        publish(cardMenu); // Gửi cardMenu đến phương thức process
                    }
                }
                return null;
            }

            @Override
            protected void process(List<CardMenu> chunks) {
                for (CardMenu cardMenu : chunks) {
                    menFormUI.getPanelCard().add(cardMenu);
                }
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    menFormUI.getPanelCard().revalidate();
                    menFormUI.getPanelCard().repaint();
                });
            }
        };

        cardWorker.execute();
    }

    /** 
     * Populates the basic table with the fetched Menu objects.
     * 
     * @param menus The list of Menu objects to populate.
     */
    private void populateBasicMenu(List<Menu> menus) {
        SwingWorker<Void, Menu> tableWorker = new SwingWorker<Void, Menu>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<List<Menu>> groupedMenus = sortAndGroupMenus(menus);
                for (List<Menu> group : groupedMenus) {
                    for (Menu menu : group) {
                        publish(menu); // Gửi menu đến phương thức process
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Menu> chunks) {
                for (Menu menu : chunks) {
                    System.out.println("Đang load table: " + menu.getName());
                    menFormUI.getTableModel().addRow(menuController.toTableRow(menu));
                }
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    menFormUI.getTableModel().fireTableDataChanged(); // Cập nhật table
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
     * Handles the search text change with debounce functionality.
     * 
     * @param txtSearch The JTextField containing the search text.
     */
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
            case "create-promotion":
                showCreatePromotionModal();
                break;
            default:
                break;
        }
    }

    private void showDetailsModal() {
        String[] idHolder = { "" };
        if (menFormUI.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("view details"))
                return;
            idHolder[0] = menFormUI.getTable().getValueAt(menFormUI.getTable().getSelectedRow(), 1).toString();
        } else if (menFormUI.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "view details"))
                return;
        }
        MenuInfoForm infoFormMenu = createInfoFormMenu(idHolder[0]);
        ModalDialog.showModal(menFormUI, new AdaptSimpleModalBorder(infoFormMenu, "Menu details information", AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {}), DefaultComponent.getInfoForm());
    }

    /** 
     * Displays a modal dialog for creating a new menu.
     */
    private void showCreateModal() {
        CreateMenuInputForm inputFormCreateMenu = new CreateMenuInputForm();
        ModalDialog.showModal(menFormUI, new AdaptSimpleModalBorder(inputFormCreateMenu, "Create menu", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleCreateMenu(inputFormCreateMenu);
            }
        }), DefaultComponent.getInputForm());
    }

    private void showCreatePromotionModal() {
        CreatePromotionInputForm inputFormCreatePromotion = new CreatePromotionInputForm(getSelectedMenuIdsForDeletion());
        ModalDialog.showModal(menFormUI, new AdaptSimpleModalBorder(inputFormCreatePromotion, "Create promotion", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleCreatePromotion(inputFormCreatePromotion);
            }
        }), DefaultComponent.getInputFormDoubleSize());
    }

    private void handleCreatePromotion(CreatePromotionInputForm inputFormCreatePromotion) {
        try {
            promotionController.createPromotions(inputFormCreatePromotion.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawer.setSelectedItemClass(PromotionFormUI.class);
    }

    private void handleCreateMenu(CreateMenuInputForm inputFormCreateMenu) {
        Object[] menuData = inputFormCreateMenu.getData();
        menuController.createMenu(menuData);
		Toast.show(menFormUI, Toast.Type.SUCCESS, "Create menu successfully");
		loadData();
    }

    private void showEditModal() {
        String[] idHolder = { "" };
        if (menFormUI.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("edit"))
                return;
            idHolder[0] = menFormUI.getTable().getValueAt(menFormUI.getTable().getSelectedRow(), 1).toString();
        } else if (menFormUI.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "edit"))
                return;
        }
        Menu menu = null;
        menu = null;// menuController.getMenuById(idHolder[0]);
        UpdateMenuInputForm inputFormUpdateMenu = createInputFormUpdateMenu(menu);
        ModalDialog.showModal(menFormUI, new AdaptSimpleModalBorder(inputFormUpdateMenu, "Update menu", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleUpdateMenu(inputFormUpdateMenu);
            }
        }), DefaultComponent.getInputForm());
    }

    private boolean validateSingleRowSelection(String action) {
        if (menFormUI.getTable().getSelectedRowCount() > 1) {
            Toast.show(menFormUI, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (menFormUI.getTable().getSelectedRowCount() == 0) {
            Toast.show(menFormUI, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        return true;
    }

    private boolean validateSingleCardSelection(String[] idHolder, String action) {
        List<String> selectedIds = findSelectedMenuIds(menFormUI.getPanelCard());
        if (selectedIds.size() > 1) {
            Toast.show(menFormUI, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (selectedIds.isEmpty()) {
            Toast.show(menFormUI, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        idHolder[0] = selectedIds.get(0);
        return true;
    }

    private UpdateMenuInputForm createInputFormUpdateMenu(Menu menu) {
        try {
            return new UpdateMenuInputForm(menu);
        } catch (IOException e) {
            Toast.show(menFormUI, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
            return null;
        }
    }

    private MenuInfoForm createInfoFormMenu(String menuId) {
        try {
            return new MenuInfoForm(menuId);
        } catch (IOException e) {
            Toast.show(menFormUI, Toast.Type.ERROR, "Failed to find menu to view details: " + e.getMessage());
            return null;
        }
    }

    private void handleUpdateMenu(UpdateMenuInputForm inputFormUpdateMenu) {
        Object[] menuData = inputFormUpdateMenu.getData();
        menuController.updateMenu(menuData);
		Toast.show(menFormUI, Toast.Type.SUCCESS, "Update menu successfully");
		loadData();
    }

    private void showDeleteModal() {
        List<String> findSelectedMenuIds = getSelectedMenuIdsForDeletion();
        if (findSelectedMenuIds.isEmpty()) {
            Toast.show(menFormUI, Toast.Type.ERROR, "You have to select at least one menu to delete");
            return;
        }
        confirmDeletion(findSelectedMenuIds);
    }

    private List<String> getSelectedMenuIdsForDeletion() {
        if (menFormUI.getSelectedTitle().equals("Basic table")) {
            return findSelectedMenuIds();
        } else if (menFormUI.getSelectedTitle().equals("Grid table")) {
            return findSelectedMenuIds(menFormUI.getPanelCard());
        }
        return new ArrayList<>();
    }

    private void confirmDeletion(List<String> findSelectedMenuIds) {
        if (findSelectedMenuIds.size() == 1) {
            confirmSingleDeletion(findSelectedMenuIds.get(0));
        } else {
            confirmMultipleDeletion(findSelectedMenuIds);
        }
    }

    private void confirmSingleDeletion(String menuId) {
        ModalDialog.showModal(menFormUI, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete this menu: " + menuId + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deleteMenu(menuId);
            }
        }), DefaultComponent.getChoiceModal());
    }

    private void confirmMultipleDeletion(List<String> findSelectedMenuIds) {
        ModalDialog.showModal(menFormUI, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete these menus: " + findSelectedMenuIds + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deleteMenus(findSelectedMenuIds);
            }
        }), DefaultComponent.getChoiceModal());
    }

    private void deleteMenu(String menuId) {
        menuController.deleteMenu(menuId);
		loadData(); // Reload menu data after successful deletion
		Toast.show(menFormUI, Toast.Type.SUCCESS, "Delete menu successfully");
    }

    private void deleteMenus(List<String> findSelectedMenuIds) {
        menuController.deleteMenus(findSelectedMenuIds);
		loadData(); // Reload menu data after successful deletion
		Toast.show(menFormUI, Toast.Type.SUCCESS, "Delete menus successfully");
    }

    private static final int CHUNK_SIZE = 4;

    public List<String> findSelectedMenuIds() {
        int rowCount = menFormUI.getTable().getRowCount();
        List<String> menuIdsToDelete = new ArrayList<>();
        List<List<String>> chunks = createChunks(rowCount);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
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

    private List<List<String>> createChunks(int rowCount) {
        List<List<String>> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            String menuId = menFormUI.getTable().getValueAt(i, 1).toString();
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

    private List<String> processChunk(List<String> chunk) {
        List<String> menuIdsToDelete = new ArrayList<>();

        for (String menuId : chunk) {
            int rowIndex = findRowIndexById(menuId);
            Boolean isChecked = (Boolean) menFormUI.getTable().getValueAt(rowIndex, 0);
            if (isChecked != null && isChecked) {
                menuIdsToDelete.add(menuId);
            }
        }

        return menuIdsToDelete;
    }

    private int findRowIndexById(String menuId) {
        for (int i = 0; i < menFormUI.getTable().getRowCount(); i++) {
            if (menFormUI.getTable().getValueAt(i, 1).equals(menuId)) {
                return i;
            }
        }
        return -1;
    }

    public List<String> findSelectedMenuIds(JPanel panelCard) {
        List<String> selectedMenuIds = new ArrayList<>();
        Component[] components = panelCard.getComponents();
        List<List<Component>> chunks = createComponentChunks(components);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
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

    private List<String> processComponentChunk(List<Component> chunk) {
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
	    loadData();
	}

	public int getTotalPages() {
	    fetchMenus();
	    return totalPages;
	}
}