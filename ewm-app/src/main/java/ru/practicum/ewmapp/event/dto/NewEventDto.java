package ru.practicum.ewmapp.event.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.commondtolib.JacksonLocalDateTimeSerializer;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.util.TwoHoursOrMoreFromNow;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
@JsonDeserialize(using = JacksonNewEventDtoDeserializer.class)
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Long category;
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;
    @TwoHoursOrMoreFromNow
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private int participantLimit;
    private Boolean requestModeration;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
    private Boolean permitComments;

    public NewEventDto(String annotation, Long category, String description,
                       LocalDateTime eventDate, Location location, Boolean paid,
                       int participantLimit, Boolean requestModeration,
                       String title) {
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.title = title;
    }
}
