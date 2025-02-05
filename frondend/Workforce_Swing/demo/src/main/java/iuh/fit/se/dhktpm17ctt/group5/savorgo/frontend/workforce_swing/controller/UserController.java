package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserRoleEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserTierEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.UserService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.UserServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;

public class UserController  {

    // Using a service implementation for handling user operations
    private UserService serviceUser  = new UserServiceImpl();

    /**
     * Retrieves a list of all users.
     * 
     * @return List of all users.
     * @throws IOException if there is an issue during the process.
     */
    public List<User> getAllUsers() throws IOException {
        return serviceUser .getAllUsers();
    }

    /**
     * Retrieves a user by its ID.
     * 
     * @param id The ID of the user to retrieve.
     * @return The user with the specified ID.
     * @throws IOException if there is an issue during the process.
     */
    public User getUserById(String id) throws IOException {
        return serviceUser.getUserById(id);
    }

    /**
     * Creates a new user with the specified details.
     * 
     * @param userData An array containing the details of the user.
     *                  [0] - email, [1] - first name, [2] - last name,
     *                  [3] - role, [4] - points, [5] - tier, [6] - image path.
     * @throws IOException if there is an issue during the process.
     */
    public void createUser (Object[] userData) throws IOException {
        String publicId = null;
        try {
            publicId = MyImageIcon.updateImageToCloud("Users", new File(userData[6].toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = User.builder()
                .email(userData[0].toString())
                .firstName(userData[1].toString())
                .lastName(userData[2].toString())
                .role((UserRoleEnum) userData[3])
                .points((int) userData[4])
                .tier((UserTierEnum)userData[5])
                .publicId(publicId)
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .build();
        serviceUser .createUser (user);
    }

    /**
     * Updates an existing user with the specified details.
     * 
     * @param userData An array containing the updated details of the user.
     *                  [0] - id, [1] - email, [2] - first name, [3] - last name,
     *                  [4] - role, [5] - points, [6] - tier, [7] - image path.
     * @throws IOException       if there is an issue during the process.
     * @throws BusinessException if the user does not exist.
     */
    public void updateUser (Object[] userData) throws IOException, BusinessException {
        User user = getUserById(userData[0].toString());
        user.setEmail(userData[1].toString());
        user.setFirstName(userData[2].toString());
        user.setLastName(userData[3].toString());
        user.setRole((UserRoleEnum) userData[4]);
        user.setPoints((int) userData[5]);
        user.setTier((UserTierEnum)userData[6]);

        if (userData[7] != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> {
                return MyImageIcon.updateImageToCloud("Users", new File(userData[7].toString()));
            });
            try {
                String publicId = future.get(); // Wait for the task to complete
                user.setPublicId(publicId);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown(); // Ensure the ExecutorService is closed
            }
        }

        user.setModifiedTime(LocalDateTime.now());
        serviceUser .updateUser (user);
    }

    /**
     * Deletes a user by its ID.
     * 
     * @param id The ID of the user to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteUser (String id) throws IOException {
        serviceUser .deleteUser (id);
    }

    /**
     * Deletes multiple users by their IDs.
     * 
     * @param userIds The list of IDs of the users to delete.
     * @throws IOException if there is an issue during the process.
     */
    public void deleteUsers(List<String> userIds) throws IOException {
        serviceUser .deleteUsers(userIds);
    }

    /**
     * Searches for users based on a search term.
     * 
     * @param search The search term to filter users.
     * @return List of users matching the search term.
     * @throws IOException if there is an issue during the process.
     */
    public List<User> searchUsers(String search) throws IOException {
        return serviceUser .searchUsers(search);
    }
    
    /**
     * Converts a User object to an array of objects for table display.
     * 
     * @param user The User object to convert.
     * @return An array of objects suitable for table display.
     */
    public Object[] toTableRow(User user) {
        return new Object[]{
            false, // Checkbox for selection
            user.getId(),
            user.getFirstName() + " " + user.getLastName(), // Full name
            user.getRole().getDisplayName(), // Role display name
            user.getEmail(),
            user.getPoints(),
            user.getTier().getDisplayName() // Tier display name
        };
    }
}