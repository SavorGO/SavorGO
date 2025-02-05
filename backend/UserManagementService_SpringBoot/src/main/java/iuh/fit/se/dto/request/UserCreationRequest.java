package iuh.fit.se.dto.request;

import iuh.fit.se.enums.Role;
import iuh.fit.se.enums.Status;
import iuh.fit.se.enums.Tier;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String email;
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
}
