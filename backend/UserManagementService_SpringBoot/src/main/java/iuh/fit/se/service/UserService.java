package iuh.fit.se.service;

import iuh.fit.se.dto.request.UserCreationRequest;
import iuh.fit.se.dto.request.UserUpdateRequest;
import iuh.fit.se.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    UserResponse updateUser(String email, UserUpdateRequest request);
    void deleteUser(String id);
    UserResponse findByEmail(String email);
    List<UserResponse> findUsers();

    List<UserResponse> findByRole(String role);
}
