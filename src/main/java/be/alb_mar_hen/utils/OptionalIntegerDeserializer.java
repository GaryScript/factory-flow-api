package be.alb_mar_hen.utils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

public class OptionalIntegerDeserializer extends JsonDeserializer<Optional<Integer>> {
    @Override
    public Optional<Integer> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // Parse la valeur JSON et renvoie un Optional
        if (p.getCurrentToken().isNumeric()) {
            return Optional.of(p.getIntValue());
        } else if (p.getCurrentToken() == null) {
            return Optional.empty();
        } else {
        	throw ctxt.instantiationException(getClass(), "Expected integer or null for Optional<Integer>");
        }
    }
}
