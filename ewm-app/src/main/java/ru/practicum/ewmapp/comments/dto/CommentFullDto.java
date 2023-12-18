package ru.practicum.ewmapp.comments.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.commondtolib.JacksonLocalDateTimeSerializer;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime createdOn;
    private EventShortDto event;
    private UserDto commentator;
    private String text;
    private UserState userState;
    private CommentState commentState;
}
