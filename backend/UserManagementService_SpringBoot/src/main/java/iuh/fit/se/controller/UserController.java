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

import com.nimbusds.jose.shaded.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(){
        return ResponseEntity.ok(userService.findUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id){
	    return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/search/r/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role){
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/search/e/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.findByEmail(email));
    }


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(userService.updateUser(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User has been deleted");
    }
    
    @DeleteMapping
    public ResponseEntity<String> deleteUsers(@RequestBody Map<String, List<String>> requestBody) {
        List<String> ids = requestBody.get("ids");
        userService.deleteUsers(ids);
        return ResponseEntity.ok("Users have been deleted");
    }
}