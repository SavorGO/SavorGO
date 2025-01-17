package raven.modal.demo.utils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Chuyển LocalDateTime thành chuỗi ISO 8601
        String formattedDate = value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        gen.writeString(formattedDate + "Z"); // Thêm 'Z' cho UTC
    }
}
