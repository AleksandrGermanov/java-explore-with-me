package ru.practicum.ewmapp.event.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.ewmapp.event.model.Location;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class JacksonNewEventDtoDeserializer extends StdDeserializer<NewEventDto> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JacksonNewEventDtoDeserializer() {
        this(null);
    }

    protected JacksonNewEventDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public NewEventDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String annotation = node.get("annotation").asText(null);
        Long category = node.get("category") == null ? null
                : node.get("category").asLong();
        String description = node.get("description").asText(null);
        LocalDateTime eventDate = node.get("eventDate") == null ? null
                : LocalDateTime.parse(node.get("eventDate").asText(), formatter);
        Location location = node.get("location") == null ? null
                : extractLocation(node);
        Boolean paid = node.get("paid").asBoolean(false);
        Integer participantLimit = node.get("participantLimit").asInt(0);
        Boolean requestModeration = node.get("requestModeration").asBoolean(true);
        String title = node.get("title").asText(null);
        return new NewEventDto(annotation, category, description, eventDate,
                location, paid, participantLimit, requestModeration, title);
    }

    private Location extractLocation(JsonNode node) {
        Float lat = node.get("location").get("lat") == null ? null
                : node.get("location").get("lat").floatValue();
        Float lon = node.get("location").get("lon") == null ? null
                : node.get("location").get("lon").floatValue();
        return new Location(lon, lat);
    }
}
