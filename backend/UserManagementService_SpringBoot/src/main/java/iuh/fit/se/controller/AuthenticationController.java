package iuh.fit.se.controller;

import iuh.fit.se.entity.User;
import iuh.fit.se.service.impl.AuthenticationServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {
    AuthenticationServiceImpl authenticationService;
    @GetMapping("/{email}")
    public String generateToken(@PathVariable String email){
        return authenticationService.generateToken(email);
    }
}
