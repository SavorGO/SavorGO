package iuh.fit.se.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.dto.request.UserCreationRequest;
import iuh.fit.se.dto.request.UserUpdateRequest;
import iuh.fit.se.dto.response.UserResponse;
import iuh.fit.se.entity.User;
import iuh.fit.se.enums.Role;
import iuh.fit.se.repository.UserRepository;
import iuh.fit.se.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    
    @Override
    public UserResponse createUser(UserCreationRequest request) {
        User user = objectMapper.convertValue(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return objectMapper.convertValue(userRepository.save(user), UserResponse.class);
    }

    @Override
    public UserResponse updateUser(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Cập nhật thông tin từ request
        try {
			objectMapper.updateValue(user, request);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        return objectMapper.convertValue(userRepository.save(user), UserResponse.class);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return objectMapper.convertValue(user, UserResponse.class);
    }

    @Override
    public List<UserResponse> findUsers() {
        List<User> users = userRepository.findAll();
        return objectMapper.convertValue(users, new TypeReference<List<UserResponse>>() {});
    }

    @Override
    public List<UserResponse> findByRole(String role) {
        Role roleEnum = Role.valueOf(role.toUpperCase());
        List<User> users = userRepository.findByRole(roleEnum);
        return objectMapper.convertValue(users, new TypeReference<List<UserResponse>>() {});
    }

	@Override
	public UserResponse findById(String id) {
		Optional<User> user = userRepository.findById(id);
		return objectMapper.convertValue(user.get(), UserResponse.class);
	}
}
