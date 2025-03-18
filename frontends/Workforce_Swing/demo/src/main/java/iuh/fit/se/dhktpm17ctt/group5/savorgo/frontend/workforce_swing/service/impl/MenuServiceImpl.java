package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.MenuService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpUtil;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.JsonUtil;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the MenuService interface for interacting with the menu API.
 * This class uses HttpUtil and JsonUtil for HTTP requests and JSON processing.
 */
public class MenuServiceImpl implements MenuService {

    private static final String API_URL = "http://localhost:8000/api/menus";
    private static final String API_URL_ID = API_URL + "/";

    /**
     * Retrieves all menus from the backend API.
     *
     * @return A list of Menu objects.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Menu> getAllMenus() throws IOException {
        Response response = HttpUtil.get(API_URL);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Menu[] menus = JsonUtil.fromJson(response.body().string(), Menu[].class);
        return Arrays.asList(menus);
    }

    /**
     * Retrieves a menu by its ID from the backend API.
     *
     * @param id The ID of the menu to retrieve.
     * @return The Menu object.
     * @throws IOException If the request fails.
     */
    @Override
    public Menu getMenuById(String id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return JsonUtil.fromJson(response.body().string(), Menu.class);
    }

    /**
     * Creates a new menu in the backend API.
     *
     * @param menu The Menu object to create.
     * @throws IOException If the request fails.
     */
    @Override
    public void createMenu(Menu menu) throws IOException {
        String jsonBody = JsonUtil.toJson(menu);
        Response response = HttpUtil.post(API_URL, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Updates an existing menu in the backend API.
     *
     * @param menu The Menu object to update.
     * @throws IOException If the request fails.
     */
    @Override
    public void updateMenu(Menu menu) throws IOException {
        String jsonBody = JsonUtil.toJson(menu);
        String url = API_URL_ID + menu.getId();
        Response response = HttpUtil.put(url, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Deletes a menu by its ID from the backend API.
     *
     * @param id The ID of the menu to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deleteMenu(String id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.delete(url);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete menu with ID: " + id + ". Unexpected code: " + response.code());
        }
    }

    /**
     * Deletes multiple menus by their IDs from the backend API.
     *
     * @param ids The list of IDs of the menus to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deleteMenus(List<String> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        Response response = HttpUtil.deleteWithBody(API_URL, json);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete menus. Unexpected code: " + response.code());
        }
    }

    /**
     * Searches for menus based on a search term in the backend API.
     *
     * @param search The search term.
     * @return A list of Menu objects that match the search term.
     * @throws IOException If the request fails.
     */
    @Override
    public List<Menu> searchMenus(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        Menu[] menus = JsonUtil.fromJson(response.body().string(), Menu[].class);
        return Arrays.asList(menus);
    }
}