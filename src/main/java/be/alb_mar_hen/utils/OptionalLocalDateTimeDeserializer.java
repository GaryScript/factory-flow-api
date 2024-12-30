package be.alb_mar_hen.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class OptionalLocalDateTimeDeserializer extends JsonDeserializer<Optional<LocalDateTime>> {
    @Override
    public Optional<LocalDateTime> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isNull()) {
            return Optional.empty();
        } else {
            int year = node.get("year").asInt();
            int monthValue = node.get("monthValue").asInt();
            int dayOfMonth = node.get("dayOfMonth").asInt();
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            int second = node.get("second").asInt();
            int nano = node.get("nano").asInt();

            LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second, nano);
            return Optional.of(dateTime);
        }
    }
}
