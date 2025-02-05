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

    @GetMapping("/search/r/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role){
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/search/e/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.findByEmail(email));
    }


    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreationRequest request){
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String email, @RequestBody UserUpdateRequest request){
        return ResponseEntity.ok(userService.updateUser(email,request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User has been deleted");
    }
}