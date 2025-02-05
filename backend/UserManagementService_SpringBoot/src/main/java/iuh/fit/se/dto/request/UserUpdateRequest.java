package iuh.fit.se.dto.request;

import iuh.fit.se.enums.Role;
import iuh.fit.se.enums.Status;
import iuh.fit.se.enums.Tier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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
}
