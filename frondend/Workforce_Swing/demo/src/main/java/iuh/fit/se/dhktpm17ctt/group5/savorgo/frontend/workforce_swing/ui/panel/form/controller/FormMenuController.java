package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.controller;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.ControllerMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller.ControllerPromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.card.CardMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.MenuFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.panel.form.PromotionFormUI;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.InputFormCreateMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.create.InputFormCreatePromotion;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.info.InfoFormMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.scrollpane.popupform.update.InputFormUpdateMenu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.simple.SimpleMessageModal;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.DefaultComponent;
import raven.modal.Drawer;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.AdaptSimpleModalBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
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

public class FormMenuController {
    private MenuFormUI formMenu;
    private ControllerMenu controllerMenu = new ControllerMenu();
    private ControllerPromotion controllerPromotion = new ControllerPromotion();
    private static final int DEBOUNCE_DELAY = 1000;
    private Timer debounceTimer;
    private volatile boolean isLoading = false;

    /** 
     * Constructs a FormMenuController with the specified MenuFormUI.
     * 
     * @param formMenu The MenuFormUI instance to control.
     */
    public FormMenuController(MenuFormUI formMenu) {
        this.formMenu = formMenu;
    }

    /** 
     * Loads data based on the search term.
     * 
     * @param searchTerm the term to search for menus.
     */
    public void loadData(String searchTerm) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                List<Menu> menus = fetchMenus(searchTerm);
                if (menus == null || menus.isEmpty()) {
                    Toast.show(formMenu, Toast.Type.INFO, "No menu in database or in search");
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    formMenu.getPanelCard().removeAll();
                    try {
                        populateCardMenu(menus);
                    } catch (IOException e) {
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
                List<Menu> menus = fetchMenus(searchTerm);
                SwingUtilities.invokeLater(() -> {
                    formMenu.getTableModel().setRowCount(0);
                    populateBasicMenu(menus);
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
                SwingUtilities.invokeLater(() -> {
                    executor.shutdown();
                    System.gc();
                    isLoading = false;
                });
            }
        }).start();
    }

    /** 
     * Fetches the list of menus based on the search term.
     * 
     * @param searchTerm The term to search for in the menu.
     * @return The list of Menu objects.
     * @throws IOException If an I/O error occurs.
     */
    private List<Menu> fetchMenus(String searchTerm) throws IOException {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return controllerMenu.searchMenus(searchTerm);
        } else {
            return controllerMenu.getAllMenus();
        }
    }

    /** 
     * Populates the card menu with the fetched Menu objects.
     * 
     * @param menus The list of Menu objects to populate.
     * @throws IOException If an I/O error occurs during population.
     */
    private void populateCardMenu(List<Menu> menus) throws IOException {
        List<List<Menu>> groupedMenus = sortAndGroupMenus(menus);
        for (List<Menu> group : groupedMenus) {
            for (Menu modelMenu : group) {
                CardMenu cardMenu = new CardMenu(modelMenu);
                cardMenu.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        showPopup(e);
                    }

                    private void showPopup(MouseEvent e) {
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
                                formMenu.createPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
                });
                SwingUtilities.invokeLater(() -> formMenu.getPanelCard().add(cardMenu));
            }
        }
        SwingUtilities.invokeLater(() -> {
            formMenu.getPanelCard().revalidate();
            formMenu.getPanelCard().repaint();
        });
    }

    /** 
     * Populates the basic menu with the fetched Menu objects.
     * 
     * @param menus The list of Menu objects to populate.
     */
    private void populateBasicMenu(List<Menu> menus) {
        List<List<Menu>> groupedMenus = sortAndGroupMenus(menus);
        SwingUtilities.invokeLater(() -> formMenu.getTableModel().setRowCount(0));
        groupedMenus.forEach(group -> {
            group.forEach(modelMenu -> {
                SwingUtilities.invokeLater(() -> {
                    try {
                        formMenu.getTableModel().addRow(modelMenu.toTableRowBasic());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    /** 
     * Sorts and groups the menus based on their status.
     * 
     * @param menus The list of Menu objects to sort and group.
     * @return A list containing two lists: available menus and other menus.
     */
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
        debounceTimer = new Timer(DEBOUNCE_DELAY, evt -> loadData(txtSearch.getText()));
        debounceTimer.setRepeats(false);
        debounceTimer.start();
    }

    /** 
     * Displays a modal dialog for creating, editing, or deleting a menu.
     * 
     * @param userAction The action to perform (create, edit, delete).
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
            case "create-promotion":
                showCreatePromotionModal();
                break;
            default:
                break;
        }
    }

    /** 
     * Displays a modal dialog for showing details of a selected menu.
     */
    private void showDetailsModal() {
        String[] idHolder = { "" };
        if (formMenu.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("view details"))
                return;
            idHolder[0] = formMenu.getTable().getValueAt(formMenu.getTable().getSelectedRow(), 1).toString();
        } else if (formMenu.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "view details"))
                return;
        }
        InfoFormMenu infoFormMenu = createInfoFormMenu(idHolder[0]);
        ModalDialog.showModal(formMenu, new AdaptSimpleModalBorder(infoFormMenu, "Menu details information", AdaptSimpleModalBorder.DEFAULT_OPTION, (controller, action) -> {}), DefaultComponent.getInfoForm());
    }

    /** 
     * Displays a modal dialog for creating a new menu.
     */
    private void showCreateModal() {
        InputFormCreateMenu inputFormCreateMenu = new InputFormCreateMenu();
        ModalDialog.showModal(formMenu, new AdaptSimpleModalBorder(inputFormCreateMenu, "Create menu", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleCreateMenu(inputFormCreateMenu);
            }
        }), DefaultComponent.getInputForm());
    }

    /** 
     * Displays a modal dialog for creating a new promotion.
     */
    private void showCreatePromotionModal() {
        InputFormCreatePromotion inputFormCreatePromotion = new InputFormCreatePromotion(getSelectedMenuIds());
        ModalDialog.showModal(formMenu, new AdaptSimpleModalBorder(inputFormCreatePromotion, "Create promotion", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleCreatePromotion(inputFormCreatePromotion);
            }
        }), DefaultComponent.getInputFormDoubleSize());
    }

    /** 
     * Handles the creation of a new promotion.
     * 
     * @param inputFormCreatePromotion The input form containing the promotion data.
     */
    private void handleCreatePromotion(InputFormCreatePromotion inputFormCreatePromotion) {
        try {
            controllerPromotion.createPromotions(inputFormCreatePromotion.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawer.setSelectedItemClass(PromotionFormUI.class);
    }

    /** 
     * Handles the creation of a new menu.
     * 
     * @param inputFormCreateMenu The input form containing the menu data.
     */
    private void handleCreateMenu(InputFormCreateMenu inputFormCreateMenu) {
        Object[] menuData = inputFormCreateMenu.getData();
        try {
            controllerMenu.createMenu(menuData);
            Toast.show(formMenu, Toast.Type.SUCCESS, "Create menu successfully");
            loadData("");
        } catch (IOException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to create menu: " + e.getMessage());
        }
    }

    /** 
     * Displays a modal dialog for editing an existing menu.
     */
    private void showEditModal() {
        String[] idHolder = { "" };
        if (formMenu.getSelectedTitle().equals("Basic table")) {
            if (!validateSingleRowSelection("edit"))
                return;
            idHolder[0] = formMenu.getTable().getValueAt(formMenu.getTable().getSelectedRow(), 1).toString();
        } else if (formMenu.getSelectedTitle().equals("Grid table")) {
            if (!validateSingleCardSelection(idHolder, "edit"))
                return;
        }
        Menu menu = null;
        try {
            menu = controllerMenu.getMenuById(idHolder[0]);
        } catch (IOException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
        }
        InputFormUpdateMenu inputFormUpdateMenu = createInputFormUpdateMenu(menu);
        ModalDialog.showModal(formMenu, new AdaptSimpleModalBorder(inputFormUpdateMenu, "Update menu", AdaptSimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                handleUpdateMenu(inputFormUpdateMenu);
            }
        }), DefaultComponent.getInputForm());
    }

    /** 
     * Validates if a single row is selected in the basic table.
     * 
     * @param action The action to perform (view details, edit, etc.).
     * @return True if a single row is selected, false otherwise.
     */
    private boolean validateSingleRowSelection(String action) {
        if (formMenu.getTable().getSelectedRowCount() > 1) {
            Toast.show(formMenu, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (formMenu.getTable().getSelectedRowCount() == 0) {
            Toast.show(formMenu, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        return true;
    }

    /** 
     * Validates if a single card is selected in the grid table.
     * 
     * @param idHolder The array to hold the selected menu ID.
     * @param action The action to perform (view details, edit, etc.).
     * @return True if a single card is selected, false otherwise.
     */
    private boolean validateSingleCardSelection(String[] idHolder, String action) {
        List<String> selectedIds = findSelectedMenuIds(formMenu.getPanelCard());
        if (selectedIds.size() > 1) {
            Toast.show(formMenu, Toast.Type.ERROR, "Please select only one row to " + action);
            return false;
        }
        if (selectedIds.isEmpty()) {
            Toast.show(formMenu, Toast.Type.ERROR, "Please select a row to " + action);
            return false;
        }
        idHolder[0] = selectedIds.get(0);
        return true;
    }

    /** 
     * Creates an input form for updating a menu.
     * 
     * @param menu The Menu object to update.
     * @return The created InputFormUpdateMenu, or null if an error occurs.
     */
    private InputFormUpdateMenu createInputFormUpdateMenu(Menu menu) {
        try {
            return new InputFormUpdateMenu(menu);
        } catch (IOException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to find menu to edit: " + e.getMessage());
            return null;
        }
    }

    /** 
     * Creates an input form for displaying menu details.
     * 
     * @param menuId The ID of the menu to display.
     * @return The created InfoFormMenu, or null if an error occurs.
     */
    private InfoFormMenu createInfoFormMenu(String menuId) {
        try {
            return new InfoFormMenu(menuId);
        } catch (IOException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to find menu to view details: " + e.getMessage());
            return null;
        }
    }

    /** 
     * Handles the update of an existing menu.
     * 
     * @param inputFormUpdateMenu The input form containing the updated menu information.
     */
    private void handleUpdateMenu(InputFormUpdateMenu inputFormUpdateMenu) {
        Object[] menuData = inputFormUpdateMenu.getData();
        try {
            controllerMenu.updateMenu(menuData);
            Toast.show(formMenu, Toast.Type.SUCCESS, "Update menu successfully");
            formMenu.formRefresh();
        } catch (IOException | BusinessException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to update menu: " + e.getMessage());
        }
    }

    /** 
     * Displays a modal dialog for deleting selected menus.
     */
    private void showDeleteModal() {
        List<String> findSelectedMenuIds = getSelectedMenuIds();
        if (findSelectedMenuIds.isEmpty()) {
            Toast.show(formMenu, Toast.Type.ERROR, "You have to select at least one menu to delete");
            return;
        }
        confirmDeletion(findSelectedMenuIds);
    }

    /** 
     * Gets the IDs of the selected menus for deletion.
     * 
     * @return The list of selected menu IDs.
     */
    private List<String> getSelectedMenuIds() {
        if (formMenu.getSelectedTitle().equals("Basic table")) {
            return findSelectedMenuIds();
        } else if (formMenu.getSelectedTitle().equals("Grid table")) {
            return findSelectedMenuIds(formMenu.getPanelCard());
        }
        return new ArrayList<>();
    }

    /** 
     * Confirms the deletion of the selected menus.
     * 
     * @param findSelectedMenuIds The list of menu IDs to delete.
     */
    private void confirmDeletion(List<String> findSelectedMenuIds) {
        if (findSelectedMenuIds.size() == 1) {
            confirmSingleDeletion(findSelectedMenuIds.get(0));
        } else {
            confirmMultipleDeletion(findSelectedMenuIds);
        }
    }

    /** 
     * Confirms the deletion of a single menu.
     * 
     * @param menuId The ID of the menu to delete.
     */
    private void confirmSingleDeletion(String menuId) {
        ModalDialog.showModal(formMenu, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete this menu: " + menuId + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deleteMenu(menuId);
            }
        }), DefaultComponent.getChoiceModal());
    }

    /** 
     * Confirms the deletion of multiple menus.
     * 
     * @param findSelectedMenuIds The list of menu IDs to delete.
     */
    private void confirmMultipleDeletion(List<String> findSelectedMenuIds) {
        ModalDialog.showModal(formMenu, new SimpleMessageModal(SimpleMessageModal.Type.WARNING, "Are you sure you want to delete these menus: " + findSelectedMenuIds + "? This action cannot be undone.", "Confirm Deletion", SimpleMessageModal.YES_NO_OPTION, (controller, action) -> {
            if (action == AdaptSimpleModalBorder.YES_OPTION) {
                deleteMenus(findSelectedMenuIds);
            }
        }), DefaultComponent.getChoiceModal());
    }

    /** 
     * Deletes a single menu by its ID.
     * 
     * @param menuId The ID of the menu to delete.
     */
    private void deleteMenu(String menuId) {
        try {
            controllerMenu.deleteMenu(menuId);
            loadData(""); // Reload menu data after successful deletion
            Toast.show(formMenu, Toast.Type.SUCCESS, "Delete menu successfully");
        } catch (IOException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to delete menu: " + e.getMessage());
        }
    }

    /** 
     * Deletes multiple menus by their IDs.
     * 
     * @param findSelectedMenuIds The list of menu IDs to delete.
     */
    private void deleteMenus(List<String> findSelectedMenuIds) {
        try {
            controllerMenu.deleteMenus(findSelectedMenuIds);
            loadData(""); // Reload menu data after successful deletion
            Toast.show(formMenu, Toast.Type.SUCCESS, "Delete menus successfully");
        } catch (IOException e) {
            Toast.show(formMenu, Toast.Type.ERROR, "Failed to delete menus: " + e.getMessage());
        }
    }

    private static final int CHUNK_SIZE = 4;

    /** 
     * Finds the selected menu IDs in the basic menu.
     * 
     * @return The list of selected menu IDs.
     */
    public List<String> findSelectedMenuIds() {
        int rowCount = formMenu.getTable().getRowCount();
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

    /** 
     * Creates chunks of menu rows for processing.
     * 
     * @param rowCount The total number of rows in the menu.
     * @return The list of chunks containing menu IDs.
     */
    private List<List<String>> createChunks(int rowCount) {
        List<List<String>> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            String menuId = formMenu.getTable().getValueAt(i, 1).toString();
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

    /** 
     * Processes a chunk of menu rows and returns the list of menu IDs to delete.
     * 
     * @param chunk The list of menu IDs in the chunk.
     * @return The list of menu IDs to delete.
     */
    private List<String> processChunk(List<String> chunk) {
        List<String> menuIdsToDelete = new ArrayList<>();

        for (String menuId : chunk) {
            int rowIndex = findRowIndexById(menuId);
            Boolean isChecked = (Boolean) formMenu.getTable().getValueAt(rowIndex, 0);
            if (isChecked != null && isChecked) {
                menuIdsToDelete.add(menuId);
            }
        }

        return menuIdsToDelete;
    }

    /** 
     * Finds the row index of a menu by its ID.
     * 
     * @param menuId The ID of the menu to find.
     * @return The row index of the menu.
     */
    private int findRowIndexById(String menuId) {
        for (int i = 0; i < formMenu.getTable().getRowCount(); i++) {
            if (formMenu.getTable().getValueAt(i, 1).equals(menuId)) {
                return i;
            }
        }
        return -1;
    }

    /** 
     * Finds the selected menu IDs in the grid menu.
     * 
     * @param panelCard The JPanel containing the CardMenu components.
     * @return The list of selected menu IDs.
     */
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

    /** 
     * Creates chunks of components for processing.
     * 
     * @param components The array of components to chunk.
     * @return The list of chunks containing components.
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
     * Processes a chunk of components and returns the list of selected menu IDs.
     * 
     * @param chunk The list of components in the chunk.
     * @return The list of selected menu IDs.
     */
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
}