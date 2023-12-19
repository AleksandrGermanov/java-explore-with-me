package ru.practicum.ewmapp.comments.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;

@Service
public class CommentMapperImpl implements CommentMapper {
    @Override
    public Comment commentFromNewDto(
            Event event, User commentator, NewCommentDto dto, UserState userState) {
        return new Comment(null, LocalDateTime.now(),
                event, commentator, dto.getText(), userState, CommentState.POSTED);
    }

    @Override
    public CommentShortDto shortDtoFromComment(Comment comment, UserShortDto userDto) {
        String text = CommentState.REMOVED_BY_USER.equals(comment.getCommentState())
                ? "removed" : comment.getText();
        return new CommentShortDto(comment.getId(), comment.getCreatedOn(), comment.getEvent().getId(),
                userDto, text, comment.getUserState(), comment.getCommentState());
    }

    @Override
    public CommentFullDto fullDtoFromComment(Comment comment, EventShortDto eventShortDto, UserShortDto userDto) {
        String text = CommentState.REMOVED_BY_USER.equals(comment.getCommentState())
                ? "removed" : comment.getText();
        return new CommentFullDto(comment.getId(), comment.getCreatedOn(), eventShortDto,
                userDto, text, comment.getUserState(), comment.getCommentState());
    }
}
