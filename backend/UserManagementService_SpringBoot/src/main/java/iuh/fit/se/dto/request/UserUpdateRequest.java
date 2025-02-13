package iuh.fit.se.dto.request;

import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
<<<<<<< Updated upstream
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
=======
>>>>>>> Stashed changes

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
<<<<<<< Updated upstream
@JsonIgnoreProperties(value = {"email"})
public class UserUpdateRequest {
	String id;
    String password;
    String firstName;
    String lastName;
    UserRoleEnum role;
    int points;
    UserTierEnum tier;
    String address;
    String publicId;
    UserStatusEnum status;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
=======
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    Role role;
    int points;
    Tier tier;
    String address;
    Status status;
    LocalDate createdTime;
    LocalDate modifiedTime;
>>>>>>> Stashed changes
}
