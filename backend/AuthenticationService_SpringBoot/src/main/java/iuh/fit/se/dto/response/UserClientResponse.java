package iuh.fit.se.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserClientResponse {
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
