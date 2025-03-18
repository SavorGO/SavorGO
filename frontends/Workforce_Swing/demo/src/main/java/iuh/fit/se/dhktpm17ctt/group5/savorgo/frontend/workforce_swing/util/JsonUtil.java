package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Converts a JSON string to a Java object.
     *
     * @param json  The JSON string to convert.
     * @param clazz The class of the object to convert to.
     * @return The converted Java object.
     * @throws IOException If the conversion fails.
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Converts a Java object to a JSON string.
     *
     * @param obj The Java object to convert.
     * @return The JSON string.
     * @throws IOException If the conversion fails.
     */
    public static String toJson(Object obj) throws IOException {
        return objectMapper.writeValueAsString(obj);
    }
}