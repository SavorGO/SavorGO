package iuh.fit.se.controller;

import iuh.fit.se.dto.request.UserCreationRequest;
import iuh.fit.se.dto.request.UserUpdateRequest;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    UserService userService;


    /**
     * Get all users.
     *
     * @return a list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.findUsers());
    }

    /**
     * Get a user by ID.
     *
     * @param id the unique ID of the user
     * @return the user information
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Search users by a query string.
     *
     * @param query the search query to filter users
     * @return a list of users matching the search query
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam(name = "q") String query) {
        List<UserResponse> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role.
     *
     * @param role the role of users to search for
     * @return a list of users with the specified role
     */
    @GetMapping("/search/r/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    /**
     * Get a user by email.
     *
     * @param email the email of the user
     * @return the user information
     */
    @GetMapping("/search/e/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    /**
     * Create a new user.
     *
     * @param request the user creation request containing user details
     * @return the created user
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    /**
     * Update an existing user.
     *
     * @param id      the unique ID of the user to update
     * @param request the user update request containing updated user details
     * @return the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Delete a user by ID.
     *
     * @param id the unique ID of the user to delete
     * @return a confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User has been deleted");
    }

    /**
     * Delete multiple users.
     *
     * @param requestBody a map containing a list of user IDs to delete
     * @return a confirmation message
     */
    @DeleteMapping
    public ResponseEntity<String> deleteUsers(@RequestBody Map<String, List<String>> requestBody) {
        List<String> ids = requestBody.get("ids");
        userService.deleteUsers(ids);
        return ResponseEntity.ok("Users have been deleted");
    }
}
