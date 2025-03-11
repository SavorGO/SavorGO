package iuh.fit.se.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import iuh.fit.se.enums.UserStatusEnum;
import iuh.fit.se.enums.UserTierEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String id;

    @JsonProperty("accountId")
    String accountId;

    @NotEmpty(message = "First name must not be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "NAME_INVALID")
    @JsonProperty("firstName")
    String firstName;

    @NotEmpty(message = "Last name must not be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "NAME_INVALID")
    @JsonProperty("lastName")
    String lastName;

    int points;
    UserTierEnum tier;
    String address;
    UserStatusEnum status;
    String publicId;
    LocalDateTime createdTime;
    LocalDateTime modifiedTime;
}