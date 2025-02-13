package iuh.fit.se.entity;

import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    String password;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Enumerated(EnumType.STRING)
    UserRoleEnum role;
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
    void GenerateValue(){
        this.role = UserRoleEnum.CUSTOMER;
        this.points = 0;
        this.tier = UserTierEnum.NONE;
        this.status = UserStatusEnum.AVAILABLE;
        this.createdTime = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
    }
}
