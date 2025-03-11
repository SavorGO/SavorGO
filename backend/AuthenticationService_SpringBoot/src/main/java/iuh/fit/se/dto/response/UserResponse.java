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
    Set<RoleResponse> roles;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
//    String publicId;
}
