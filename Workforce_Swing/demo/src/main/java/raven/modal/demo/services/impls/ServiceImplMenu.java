package raven.modal.demo.services.impls;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import raven.modal.demo.models.ModelMenu;
import raven.modal.demo.services.ServiceMenu;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Implementation of the ServiceMenu interface for interacting with the menu API.
 */
public class ServiceImplMenu implements ServiceMenu {

    private static final String API_URL = "http://localhost:8000/api/menus";
    private static final String API_URL_ID = API_URL + "/";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ServiceImplMenu() {
        // Initialize OkHttpClient
        this.client = new OkHttpClient();

        // Configure ObjectMapper for snake_case properties
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    /**
     * Retrieve all menus from the server.
     *
     * @return List of all menus.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<ModelMenu> getAllMenus() throws IOException {
        Request request = new Request.Builder().url(API_URL).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ModelMenu[] menus = objectMapper.readValue(response.body().string(), ModelMenu[].class);
            return Arrays.asList(menus);
        }
    }

    /**
     * Retrieve a menu by its ID.
     *
     * @param id The ID of the menu.
     * @return The menu object.
     * @throws IOException If an I/O error occurs during the request.
     */
    @Override
    public ModelMenu getMenuById(String id) throws IOException {
        String url = API_URL_ID + id;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return objectMapper.readValue(response.body().string(), ModelMenu.class);
        }
    }

    /**
     * Create a new menu with the provided data.
     *
     * @param modelMenu The menu object containing data to create.
     * @throws IOException If an I/O error occurs during the request.
     */
    @Override
    public void createMenu(ModelMenu modelMenu) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(modelMenu);
        System.out.println(jsonBody);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    /**
     * Update an existing menu by its ID.
     *
     * @param modelMenu The updated menu object.
     * @throws IOException If an I/O error occurs during the request.
     */
    @Override
    public void updateMenu(ModelMenu modelMenu) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(modelMenu);
        Request request = new Request.Builder()
                .url(API_URL_ID + modelMenu.getId())
                .put(RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8")))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
				System.out.println(response);
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    /**
     * Delete a menu by its ID.
     *
     * @param id The ID of the menu to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    @Override
    public void deleteMenu(String id) throws IOException {
        String url = API_URL_ID + id;
        Request request = new Request.Builder().url(url).delete().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete menu with ID: " + id);
            }
        }
    }

    /**
     * Delete multiple menus by their IDs.
     *
     * @param ids List of menu IDs to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    @Override
    public void deleteMenus(List<String> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(API_URL).delete(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to delete menus. Unexpected code: " + response.code());
            }
        }
    }

    /**
     * Search for menus using a query string.
     *
     * @param search The query string.
     * @return List of matching menus.
     * @throws IOException If an I/O error occurs during the request.
     */
    @Override
    public List<ModelMenu> searchMenus(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ModelMenu[] menus = objectMapper.readValue(response.body().string(), ModelMenu[].class);
            return Arrays.asList(menus);
        }
    }
}
