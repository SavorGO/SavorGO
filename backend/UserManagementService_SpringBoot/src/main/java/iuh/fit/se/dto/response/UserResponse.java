package iuh.fit.se.dto.response;

import iuh.fit.se.enums.Role;
import iuh.fit.se.enums.Tier;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String email;
    String firstName;
    String lastName;
    Role role;
    int points;
    Tier tier;
    String address;
}
