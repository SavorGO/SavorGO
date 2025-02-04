package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.io.IOException;
import java.util.List;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;

/**
 * Interface for managing menu-related operations.
 * This interface defines methods for retrieving, creating, updating, and deleting menus.
 */
public interface MenuService {

    /**
     * Retrieves all menus from the backend API.
     *
     * @return A list of all menus.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<Menu> getAllMenus() throws IOException;

    /**
     * Retrieves a menu by its ID.
     *
     * @param id The ID of the menu to retrieve.
     * @return The menu object.
     * @throws IOException If an I/O error occurs during the request.
     */
    public Menu getMenuById(String id) throws IOException;

    /**
     * Searches for menus based on a search term.
     *
     * @param search The search term.
     * @return A list of menus that match the search term.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<Menu> searchMenus(String search) throws IOException;

    /**
     * Creates a new menu in the backend API.
     *
     * @param menu The menu object to create.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void createMenu(Menu menu) throws IOException;

    /**
     * Updates an existing menu in the backend API.
     *
     * @param menu The menu object to update.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void updateMenu(Menu menu) throws IOException;

    /**
     * Deletes a menu by its ID.
     *
     * @param id The ID of the menu to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void deleteMenu(String id) throws IOException;

    /**
     * Deletes multiple menus by their IDs.
     *
     * @param ids The list of IDs of the menus to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void deleteMenus(List<String> ids) throws IOException;
}