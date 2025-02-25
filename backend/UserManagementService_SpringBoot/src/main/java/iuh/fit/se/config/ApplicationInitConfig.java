package iuh.fit.se.config;

import iuh.fit.se.entity.User;
import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByEmail("admin").isEmpty()){
                var roles = new HashSet<UserRoleEnum>();
                roles.add(UserRoleEnum.MANAGER);
                roles.add(UserRoleEnum.STAFF);
                roles.add(UserRoleEnum.CUSTOMER);
                roles.add(UserRoleEnum.GUEST);
                User user = User.builder()
                        .email("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin");
            }
        };
    }
}
