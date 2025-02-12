package iuh.fit.se.dto.request;

import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
<<<<<<< Updated upstream
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
=======
>>>>>>> Stashed changes

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
<<<<<<< Updated upstream

public class UserCreationRequest {
	String id;
=======
public class UserCreationRequest {
>>>>>>> Stashed changes
    String email;
    String password;
    String firstName;
    String lastName;
<<<<<<< Updated upstream
    UserRoleEnum role;
    int points;
    UserTierEnum tier;
    String address;
    UserStatusEnum status;
    String publicId;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
=======
    Role role;
    int points;
    Tier tier;
    String address;
    Status status;
    LocalDate createdTime;
    LocalDate modifiedTime;
>>>>>>> Stashed changes
}
