package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class CustomDateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = p.getText();
        try {
            // Nếu có 'Z' ở cuối thì chuyển nó về UTC
            if (date.endsWith("Z")) {
                return ZonedDateTime.parse(date).toLocalDateTime();
            }
            // Nếu không có 'Z' thì sử dụng DateTimeFormatter mặc định
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IOException("Unable to parse date: " + date, e);
        }
    }
}
