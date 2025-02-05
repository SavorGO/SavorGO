package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service;

import java.io.IOException;
import java.util.List;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;

/**
 * Interface for managing user-related operations.
 * This interface defines methods for retrieving, creating, updating, and deleting users.
 */
public interface UserService {

    /**
     * Retrieves all users from the backend API.
     *
     * @return A list of all users.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<User> getAllUsers() throws IOException;

    /**
     * Retrieves a user by its ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The user object.
     * @throws IOException If an I/O error occurs during the request.
     */
    public User getUserById(String id) throws IOException;

    /**
     * Searches for users based on a search term.
     *
     * @param search The search term.
     * @return A list of users that match the search term.
     * @throws IOException If an I/O error occurs during the request.
     */
    public List<User> searchUsers(String search) throws IOException;

    /**
     * Creates a new user in the backend API.
     *
     * @param user The user object to create.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void createUser (User user) throws IOException;

    /**
     * Updates an existing user in the backend API.
     *
     * @param user The user object to update.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void updateUser (User user) throws IOException;

    /**
     * Deletes a user by its ID.
     *
     * @param id The ID of the user to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void deleteUser (String id) throws IOException;

    /**
     * Deletes multiple users by their IDs.
     *
     * @param ids The list of IDs of the users to delete.
     * @throws IOException If an I/O error occurs during the request.
     */
    public void deleteUsers(List<String> ids) throws IOException;
}