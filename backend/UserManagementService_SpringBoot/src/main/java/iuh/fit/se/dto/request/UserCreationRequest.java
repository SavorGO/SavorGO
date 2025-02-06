package iuh.fit.se.dto.request;

import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserCreationRequest {
	String id;
    String email;
    String password;
    String firstName;
    String lastName;
    UserRoleEnum role;
    int points;
    UserTierEnum tier;
    String address;
    UserStatusEnum status;
    String publicId;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
}
