package ru.practicum.ewmapp.comments.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.commondtolib.JacksonLocalDateTimeSerializer;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {
    private Long id;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime createdOn;
    private Long event;
    private UserShortDto commentator;
    private String text;
    private UserState userState;
    private CommentState commentState;
}
