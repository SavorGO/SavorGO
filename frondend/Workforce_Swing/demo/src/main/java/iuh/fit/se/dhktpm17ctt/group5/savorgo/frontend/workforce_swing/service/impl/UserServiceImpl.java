package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.UserService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.HttpUtil;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util.JsonUtil;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the UserService interface for interacting with the user API.
 * This class uses HttpUtil and JsonUtil for HTTP requests and JSON processing.
 */
public class UserServiceImpl implements UserService {

    private static final String API_URL = "http://localhost:8080/api/users";
    private static final String API_URL_ID = API_URL + "/";

    /**
     * Retrieves all users from the backend API.
     *
     * @return A list of User objects.
     * @throws IOException If the request fails.
     */
    @Override
    public List<User> getAllUsers() throws IOException {
        Response response = HttpUtil.get(API_URL);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        User[] users = JsonUtil.fromJson(response.body().string(), User[].class);
        return Arrays.asList(users);
    }

    /**
     * Retrieves a user by its ID from the backend API.
     *
     * @param id The ID of the user to retrieve.
     * @return The User object.
     * @throws IOException If the request fails.
     */
    @Override
    public User getUserById(String id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        return JsonUtil.fromJson(response.body().string(), User.class);
    }

    /**
     * Creates a new user in the backend API.
     *
     * @param user The User object to create.
     * @throws IOException If the request fails.
     */
    @Override
    public void createUser (User user) throws IOException {
        String jsonBody = JsonUtil.toJson(user);
        System.out.println(jsonBody);
        Response response = HttpUtil.post(API_URL, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Updates an existing user in the backend API.
     *
     * @param user The User object to update.
     * @throws IOException If the request fails.
     */
    @Override
    public void updateUser (User user) throws IOException {
        String jsonBody = JsonUtil.toJson(user);
        String url = API_URL_ID + user.getId();
        Response response = HttpUtil.put(url, jsonBody);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Deletes a user by its ID from the backend API.
     *
     * @param id The ID of the user to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deleteUser (String id) throws IOException {
        String url = API_URL_ID + id;
        Response response = HttpUtil.delete(url);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete user with ID: " + id + ". Unexpected code: " + response.code());
        }
    }

    /**
     * Deletes multiple users by their IDs from the backend API.
     *
     * @param ids The list of IDs of the users to delete.
     * @throws IOException If the request fails.
     */
    @Override
    public void deleteUsers(List<String> ids) throws IOException {
        String json = "{\"ids\": " + ids.toString() + "}";
        Response response = HttpUtil.deleteWithBody(API_URL, json);
        if (!response.isSuccessful()) {
            throw new IOException("Failed to delete users. Unexpected code: " + response.code());
        }
    }

    /**
     * Searches for users based on a search term in the backend API.
     *
     * @param search The search term.
     * @return A list of User objects that match the search term.
     * @throws IOException If the request fails.
     */
    @Override
    public List<User> searchUsers(String search) throws IOException {
        String url = API_URL + "/search?q=" + search;
        Response response = HttpUtil.get(url);
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        User[] users = JsonUtil.fromJson(response.body().string(), User[].class);
        return Arrays.asList(users);
    }
}