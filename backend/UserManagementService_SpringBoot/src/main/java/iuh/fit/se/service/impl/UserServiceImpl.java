package iuh.fit.se.service.impl;

import iuh.fit.se.dto.request.UserCreationRequest;
import iuh.fit.se.dto.request.UserUpdateRequest;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.entity.User;
import iuh.fit.se.enums.Role;
import iuh.fit.se.mapper.UserMapper;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    @Override
    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
//        user.setRole(Role.MANAGER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        userMapper.updateUser(user,request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse findByEmail(String email) {
        return userMapper.toUserResponse(userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found with email: " + email)));
    }

    @Override
    public List<UserResponse> findUsers() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.toUserResponse(user))
                .toList();
    }

    @Override
    public List<UserResponse> findByRole(String role) {
        Role roleEnum = Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(roleEnum).stream()
                .map(user -> userMapper.toUserResponse(user))
                .toList();
    }
}