package raven.modal.demo.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.services.ServiceMenu;
import raven.modal.demo.services.impls.ServiceImplMenu;
import raven.modal.demo.utils.BusinessException;

public class ControllerMenu {

    // Using a service implementation for handling menu operations
    private ServiceMenu serviceMenu = new ServiceImplMenu();

    /**
     * Retrieves a list of all menus.
     * 
     * @return List of all menus.
     * @throws IOException if there is an issue during the process.
     */
    public List<ModelMenu> getAllMenus() throws IOException {
        return serviceMenu.getAllMenus();
    }

    /**
     * Retrieves a menu by its ID.
     * 
     * @param id The ID of the menu to retrieve.
     * @return The menu with the specified ID.
     * @throws IOException if there is an issue during the process.
     */
    public ModelMenu getMenuById(String id) throws IOException {
        return serviceMenu.getMenuById(id);
    }

    /**
     * Creates a new menu with the specified details.
     * 
     * @param name          The name of the menu.
     * @param category      The category of the menu.
     * @param description   A description of the menu.
     * @param originalPrice The original price of the menu.
     * @param salePrice     The sale price of the menu.
     * @param imageUrl      The image URL of the menu.
     * @param status        The status of the menu.
     * @throws IOException if there is an issue during the process.
     */
    public void createMenu(String name, String category, String description, double originalPrice, double salePrice, 
                           String imageUrl, String status) throws IOException {
        ModelMenu menu = ModelMenu.builder()
                .name(name)
                .category(category)
                .description(description)
                .originalPrice(originalPrice)
                .salePrice(salePrice)
                .imageUrl(imageUrl)
                .status(status)
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .build();
        
        serviceMenu.createMenu(menu);
    }

    /**
     * Updates the details of a specific menu.
     * 
     * @param id           The ID of the menu to update.
     * @param name         The new name for the menu.
     * @param category     The new category of the menu.
     * @param description  The new description of the menu.
     * @param originalPrice The new original price of the menu.
     * @param salePrice    The new sale price of the menu.
     * @param imageUrl     The new image URL of the menu.
     * @param status       The new status of the menu.
     * @throws IOException if there is an issue during the process.
     * @throws BusinessException if business rules are violated.
     */
    public void updateMenu(String id, String name, String category, String description, double originalPrice, 
                           double salePrice, String imageUrl, String status) throws IOException, BusinessException {
        ModelMenu menu = serviceMenu.getMenuById(id);

        if (menu == null) {
            throw new BusinessException("Menu with ID " + id + " not found.");
        }

        menu.setName(name);
        menu.setCategory(category);
        menu.setDescription(description);
        menu.setOriginalPrice(originalPrice);
        menu.setSalePrice(salePrice);
        menu.setImageUrl(imageUrl);
        menu.setStatus(status);
        menu.setModifiedTime(LocalDateTime.now());

        serviceMenu.updateMenu(menu);
    }

    /**
     * Deletes a menu by its ID.
     * 
     * @param id The ID of the menu to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void removeMenu(String id) throws IOException {
        serviceMenu.removeMenu(id);
    }

    /**
     * Deletes multiple menus by their IDs.
     * 
     * @param modelIds The list of IDs of the menus to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void removeMenus(List<String> modelIds) throws IOException {
        serviceMenu.removeMenus(modelIds);
    }

    /**
     * Searches for menus based on a search term.
     * 
     * @param search The search term to filter menus.
     * @return List of menus matching the search term.
     * @throws IOException if there is an issue during the process.
     */
    public List<ModelMenu> searchMenus(String search) throws IOException {
        return serviceMenu.searchMenus(search);
    }
}
