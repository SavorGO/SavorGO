package iuh.fit.se.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonIgnoreProperties(value = {"email"})
public class UserUpdateRequest {
    String firstName;
    String lastName;
    int points;
    UserTierEnum tier;
    String address;
    String publicId;
    UserStatusEnum status;
    LocalDateTime modifiedTime;
}
