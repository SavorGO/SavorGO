package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils;

import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private int status;
    private Map<String, Object> errors;
    private Object data;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    private LocalDateTime timestamp;

    public LocalDateTime getTimestamp() {
        if (timestamp != null) {
            return timestamp.atZone(ZoneId.of("UTC"))
                           .withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
                           .toLocalDateTime();
        }
        return null;
    }
}
