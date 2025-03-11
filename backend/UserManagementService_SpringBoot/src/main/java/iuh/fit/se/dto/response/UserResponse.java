package iuh.fit.se.dto.response;

import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String accountId;
    String firstName;
    String lastName;
    int points;
    UserTierEnum tier;
    String address;
    UserStatusEnum status;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
    String publicId;
}
