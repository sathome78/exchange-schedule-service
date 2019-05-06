package me.exrates.scheduleservice.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String raw = jsonParser.getValueAsString();
        if (StringUtils.isEmpty(raw)) {
            return null;
        }
        String str = raw.replaceAll("\"", "");
        if (str.endsWith("Z")) {
            return ZonedDateTime.parse(str).toLocalDateTime();
        } else {
            try {
                return LocalDateTime.parse(str);
            } catch (DateTimeParseException ex) {
                return LocalDateTime.parse(str, FORMATTER);
            }
        }
    }
}