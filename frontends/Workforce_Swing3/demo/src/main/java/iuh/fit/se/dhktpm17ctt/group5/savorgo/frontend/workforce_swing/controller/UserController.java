package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserRoleEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.enums.UserTierEnum;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.Menu;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.model.User;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.UserService;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.service.impl.UserServiceImpl;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.MyImageIcon;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.table.ThumbnailCell;
import iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils.BusinessException;

public class UserController {

	// Using a service implementation for handling user operations
	private UserService serviceUser = new UserServiceImpl();

	/**
	 * Retrieves a list of all users.
	 * 
	 * @return List of all users.
	 * @throws IOException if there is an issue during the process.
	 */
	public List<User> getAllUsers() throws IOException {
		return serviceUser.getAllUsers();
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
            publicId = MyImageIcon.updateImageToCloud("Users", new File(userData[7].toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = User.builder()
                .email(userData[0].toString())
                .firstName(userData[1].toString())
                .lastName(userData[2].toString())
                .role((UserRoleEnum) userData[3])
                .points((int) userData[4])
                .tier((UserTierEnum) userData[5])
                .address(userData[6].toString()) // Set the address
                .publicId(publicId)
                .password(userData[8].toString()) // Set the password
                .createdTime(LocalDateTime.now())
                .modifiedTime(LocalDateTime.now())
                .build();
        serviceUser .createUser (user);
    }

	/**
	 * Updates the user information based on the provided data.
	 *
	 * @param userData An array of objects containing user information:
	 *                 - userData[0]: User ID (String)
	 *                 - userData[1]: First Name (String)
	 *                 - userData[2]: Last Name (String)
	 *                 - userData[3]: User Role (User RoleEnum)
	 *                 - userData[4]: Points (Integer)
	 *                 - userData[5]: User Tier (User TierEnum)
	 *                 - userData[6]: Address (String)
	 *                 - userData[7]: Image path (String, can be null)
	 *                 - userData[8]: Password (String, can be empty)
	 * 
	 * @throws IOException If an I/O error occurs during image upload.
	 * @throws BusinessException If there is a business logic error during the update process.
	 */
	public void updateUser (Object[] userData) throws IOException, BusinessException {
	    User user = getUserById(userData[0].toString());
	    user.setFirstName(userData[1].toString());
	    user.setLastName(userData[2].toString());
	    user.setRole((UserRoleEnum) userData[3]);
	    user.setPoints((int) userData[4]);
	    user.setTier((UserTierEnum) userData[5]);
	    user.setAddress(userData[6].toString());

	    if (userData[7] != null) { // Adjusted index for image path
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

	    // Check if the user wants to change the password
	    if (!userData[8].toString().isEmpty()) { // Adjusted index for password
	        user.setPassword(userData[8].toString());
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
	public void deleteUser(String id) throws IOException {
		serviceUser.deleteUser(id);
	}

	/**
	 * Deletes multiple users by their IDs.
	 * 
	 * @param userIds The list of IDs of the users to delete.
	 * @throws IOException if there is an issue during the process.
	 */
	public void deleteUsers(List<String> userIds) throws IOException {
		serviceUser.deleteUsers(userIds);
	}

	/**
	 * Searches for users based on a search term.
	 * 
	 * @param search The search term to filter users.
	 * @return List of users matching the search term.
	 * @throws IOException if there is an issue during the process.
	 */
	public List<User> searchUsers(String search) throws IOException {
		return serviceUser.searchUsers(search);
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
           getThumbnailCell(user),
           user.getEmail(),
           user.getRole().getDisplayName(), // Role display name
           user.getPoints(),
           user.getTier().getDisplayName(), // Tier display name
           user.getCreatedTime(), // Created time
		   user.getModifiedTime()
       };
   }
   
   /**
    * Retrieves the thumbnail cell for a user.
    * 
    * @param user The User object.
    * @return The ThumbnailCell for the user.
    */
   public ThumbnailCell getThumbnailCell(User user) {
       return new ThumbnailCell(
           user.getPublicId() == null ? null : "SavorGO/Users/" + user.getPublicId(),
           user.getFirstName() + " " + user.getLastName(),
          user.getStatus().getDisplayName(),
           null
       );
   }

   /**
    * Retrieves the image for a user.
    * 
    * @param user The User object.
    * @param height The desired height of the image.
    * @param width The desired width of the image.
    * @param round The rounding for the image corners.
    * @return The MyImageIcon for the user.
    * @throws IOException if there is an issue during the process.
    */
   public MyImageIcon getImage(User user, int height, int width, int round) throws IOException {
       if (height == 0 || width == 0) {
           // Set default values if height or width is zero
           height = 50;
           width = 50;
           round = 0;
       }
       try {
           return MyImageIcon.getMyImageIconFromCloudinaryImageTag("SavorGO/Users/" + user.getPublicId(), height, width, round);
       } catch (URISyntaxException | IOException e) {
           // Return a default image if an error occurs
           return new MyImageIcon("src/main/resources/images/system/no_image_found.png", 55, 55, 10);
       }
   }
}