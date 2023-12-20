package ru.practicum.ewmapp.event.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.commondtolib.JacksonLocalDateTimeSerializer;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime createdOn;
    private String description;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;
    private Boolean permitComments;
    private List<CommentShortDto> comments;
}
