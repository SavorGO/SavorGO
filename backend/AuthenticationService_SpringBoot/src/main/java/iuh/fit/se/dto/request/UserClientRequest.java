package iuh.fit.se.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserClientRequest {
    @JsonProperty("accountId")
    String accountId; // Thay accountid → accountId
    @JsonProperty("firstName")
    String firstName; // Thay firstname → firstName
    @JsonProperty("lastName")
    String lastName;  // Thay lastname → lastName
    String address;
    String publicId;  // Thay publicid → publicId
}
