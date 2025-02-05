package iuh.fit.se.dto.response;

import java.time.LocalDateTime;

import iuh.fit.se.enums.Role;
import iuh.fit.se.enums.Tier;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
	String id;
    String email;
    String firstName;
    String lastName;
    Role role;
    int points;
    Tier tier;
    String address;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
    String publicId;
}
