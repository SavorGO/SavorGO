package iuh.fit.se.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String email;

    @JsonIgnore
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

//    @Enumerated(EnumType.STRING)
//    UserRoleEnum role;

    Set<UserRoleEnum> roles;

    int points;

    @Enumerated(EnumType.STRING)
    UserTierEnum tier;

    String address;
    UserStatusEnum status;

    @Column(name = "created_time")
    LocalDateTime createdTime;

    @Column(name = "modified_time")
    LocalDateTime modifiedTime;

    @Column(name = "public_id")
    String publicId;

    @PrePersist
    void GenerateValue() {
        if (this.roles == null || this.roles.isEmpty()) {
            this.roles = new HashSet<>();
            this.roles.add(UserRoleEnum.CUSTOMER);
        }else{
            this.roles = roles;
        }
        this.points = 0;
        this.tier = UserTierEnum.NONE;
        this.status = UserStatusEnum.AVAILABLE;
        this.createdTime = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
    }
}
