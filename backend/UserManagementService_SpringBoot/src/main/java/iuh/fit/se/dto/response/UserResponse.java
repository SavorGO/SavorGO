package iuh.fit.se.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import iuh.fit.se.enums.UserRoleEnum;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String email;
    String firstName;
    String lastName;
    Set<RoleResponse> roles;
    int points;
    UserTierEnum tier;
    String address;
    UserStatusEnum status;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
    String publicId;
}
