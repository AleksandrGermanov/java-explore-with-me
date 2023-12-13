package ru.practicum.ewmapp.event.moderation;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewmapp.event.model.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Data
public class UpdateEventRequest {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;

    public UpdateEventRequest(String annotation, Long category, String description, String eventDate,
                              Location location, Boolean paid, Integer participantLimit, Boolean requestModeration,
                              String title) {
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate == null ? null
        : LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.title = title;
    }
}
