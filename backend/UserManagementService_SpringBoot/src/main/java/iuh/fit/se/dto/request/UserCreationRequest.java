package iuh.fit.se.dto.request;

import iuh.fit.se.enums.Role;
import iuh.fit.se.enums.Status;
import iuh.fit.se.enums.Tier;
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
    Role role;
    int points;
    Tier tier;
    String address;
    Status status;
    String publicId;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
}
