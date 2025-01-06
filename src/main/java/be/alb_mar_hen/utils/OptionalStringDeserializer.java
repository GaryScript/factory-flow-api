package be.alb_mar_hen.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

public class OptionalStringDeserializer extends JsonDeserializer<Optional<String>> {
    @Override
    public Optional<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Vérifie si la valeur est null ou vide, et retourne un Optional vide
        if (p.getCurrentToken() == null || p.getText() == null || p.getText().trim().isEmpty()) {
            return Optional.empty();
        }
        // Retourne un Optional contenant la chaîne non nulle et non vide
        return Optional.of(p.getText());
    }
}
