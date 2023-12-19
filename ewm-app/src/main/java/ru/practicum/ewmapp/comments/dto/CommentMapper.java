package ru.practicum.ewmapp.comments.dto;

import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

public interface CommentMapper {
    Comment commentFromNewDto(Event event, User commentator, NewCommentDto dto, UserState userState);

    CommentShortDto shortDtoFromComment(Comment comment, UserShortDto userShortDto);

    CommentFullDto fullDtoFromComment(Comment comment, EventShortDto eventShortDto, UserShortDto userShortDto);
}
