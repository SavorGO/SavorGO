package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.EnumMenuCategory;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.EnumMenuStatus;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.MenuService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.MenuServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;

public class ControllerMenu {

    // Using a service implementation for handling menu operations
    private MenuService serviceMenu = new MenuServiceImpl();

    /**
     * Retrieves a list of all menus.
     * 
     * @return List of all menus.
     * @throws IOException if there is an issue during the process.
     */
    public List<Menu> getAllMenus() throws IOException {
        return serviceMenu.getAllMenus();
    }

    /**
     * Retrieves a menu by its ID.
     * 
     * @param id The ID of the menu to retrieve.
     * @return The menu with the specified ID.
     * @throws IOException if there is an issue during the process.
     */
    public Menu getMenuById(String id) throws IOException {
        return serviceMenu.getMenuById(id);
    }

    /**
     * Creates a new menu with the specified details.
     * 
     * @param menuData An array containing the details of the menu.
     *                  [0] - name, [1] - category, [2] - original price,
     *                  [3] - sale price, [4] - sizes, [5] - options,
     *                  [6] - image path, [7] - description.
     * @throws IOException if there is an issue during the process.
     */
    public void createMenu(Object[] menuData) throws IOException {
        String publicId = null;
        try {
            publicId = MyImageIcon.updateImageToCloud("Menus", new File(menuData[6].toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Menu menu = Menu.builder()
                .name(menuData[0].toString())
                .category((EnumMenuCategory) menuData[1])
                .originalPrice((double) menuData[2])
                .salePrice((double) menuData[3])
                .sizes((List) menuData[4])
                .options((List) menuData[5])
                .publicId(publicId)
                .description(menuData[7].toString())
                .reservedTime(LocalDateTime.now())
                .build();
        serviceMenu.createMenu(menu);
    }

    /**
     * Updates an existing menu with the specified details.
     * 
     * @param menuData An array containing the updated details of the menu.
     *                  [0] - id, [1] - name, [2] - status, [3] - category,
     *                  [4] - original price, [5] - sale price, [6] - sizes,
     *                  [7] - options, [8] - image path, [9] - description.
     * @throws IOException       if there is an issue during the process.
     * @throws BusinessException if the menu does not exist.
     */
    public void updateMenu(Object[] menuData) throws IOException, BusinessException {
        Menu menu = getMenuById(menuData[0].toString());
        menu.setName(menuData[1].toString());
        menu.setStatus((EnumMenuStatus) menuData[2]);
        menu.setCategory((EnumMenuCategory) menuData[3]);
        menu.setOriginalPrice((double) menuData[4]);
        menu.setSalePrice((double) menuData[5]);
        menu.setSizes((List) menuData[6]);
        menu.setOptions((List) menuData[7]);
        
        if (menuData[8] != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> {
                return MyImageIcon.updateImageToCloud("Menus", new File(menuData[8].toString()));
            });
            try {
                String publicId = future.get(); // Wait for the task to complete
                menu.setPublicId(publicId);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown(); // Ensure the ExecutorService is closed
            }
        }
        
        menu.setDescription(menuData[9].toString());
        serviceMenu.updateMenu(menu);
    }

    /**
     * Deletes a menu by its ID.
     * 
     * @param id The ID of the menu to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteMenu(String id) throws IOException {
        serviceMenu.deleteMenu(id);
    }

    /**
     * Deletes multiple menus by their IDs.
     * 
     * @param modelIds The list of IDs of the menus to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteMenus(List<String> modelIds) throws IOException {
        serviceMenu.deleteMenus(modelIds);
    }

    /**
     * Searches for menus based on a search term.
     * 
     * @param search The search term to filter menus.
     * @return List of menus matching the search term.
     * @throws IOException if there is an issue during the process.
     */
    public List<Menu> searchMenus(String search) throws IOException {
        return serviceMenu.searchMenus(search);
    }
}