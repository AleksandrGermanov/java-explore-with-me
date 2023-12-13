package ru.practicum.ewmapp.event.dto;

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
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String annotation = node.get("annotation") == null ? null
                : node.get("annotation").asText();
        Long category = node.get("category") == null ? null
                : node.get("category").asLong();
        String description = node.get("description") == null ? null
                : node.get("description").asText();
        LocalDateTime eventDate = node.get("eventDate") == null ? null
                : LocalDateTime.parse(node.get("eventDate").asText(), formatter);
        Location location = node.get("location") == null ? null
                : extractLocation(node);
        Boolean paid = node.get("paid") != null && node.get("paid").asBoolean();
        Integer participantLimit = node.get("participantLimit") == null ? null
                : node.get("participantLimit").asInt();
        Boolean requestModeration = node.get("requestModeration") == null || node.get("requestModeration").asBoolean();
        String title = node.get("title") == null ? null
                : node.get("title").asText();
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
