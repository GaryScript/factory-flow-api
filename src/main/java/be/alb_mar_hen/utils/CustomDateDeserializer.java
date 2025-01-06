package be.alb_mar_hen.utils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomDateDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // On récupère le node "endDateTime"
        JsonNode node = p.getCodec().readTree(p);

        // Extraire les valeurs de la date
        int year = node.get("year").asInt();
        int monthValue = node.get("monthValue").asInt();
        int dayOfMonth = node.get("dayOfMonth").asInt();
        int hour = node.get("hour").asInt();
        int minute = node.get("minute").asInt();
        int second = node.get("second").asInt();
        int nano = node.get("nano").asInt();

        // Convertir en LocalDateTime
        return LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second, nano);
    }
}
