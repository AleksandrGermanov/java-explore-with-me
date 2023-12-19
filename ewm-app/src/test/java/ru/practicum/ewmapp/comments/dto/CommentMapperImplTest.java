package ru.practicum.ewmapp.comments.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

class CommentMapperImplTest {
    private final CommentMapperImpl commentMapper = new CommentMapperImpl();
    private User user;
    private UserShortDto userShortDto;
    private Event event;
    private EventShortDto eventShortDto;
    private Comment comment;
    private NewCommentDto newCommentDto;
    private CommentShortDto commentShortDto;
    private CommentFullDto commentFullDto;


    @BeforeEach
    public void setup() {
        user = new User(0L, "name", "e@ma.il");
        userShortDto = new UserShortDto(0L, "name");
        Category category = new Category(0L, "category");
        CategoryDto categoryDto = new CategoryDto(0L, "category");
        Location location = new Location(0F, 0F);
        LocalDateTime createdOn = LocalDateTime.of(2020, 2, 2, 2, 2, 2);
        LocalDateTime publishedOn = LocalDateTime.of(2024, 2, 2, 2, 2, 2);
        LocalDateTime eventDate = LocalDateTime.of(2025, 2, 2, 2, 2, 2);
        event = new Event(0L, "annotation", category, Collections.emptyList(), createdOn,
                "description", eventDate, user, location, true, 1, publishedOn, true, EventState.PENDING,
                "title", 0L, null, null, true, null);
        event.setState(EventState.PENDING);
        eventShortDto = new EventShortDto(0L, "annotation", categoryDto, 0L,
                eventDate, userShortDto, true, "title", 0L, 0L);
        comment = new Comment(0L, publishedOn, event, user,
                "text", UserState.INITIATOR, CommentState.POSTED);
        newCommentDto = new NewCommentDto("text");
        commentShortDto = new CommentShortDto(0L, publishedOn, 0L, userShortDto, "text",
                UserState.INITIATOR, CommentState.POSTED);
        commentFullDto = new CommentFullDto(0L, publishedOn, eventShortDto,
                userShortDto, "text", UserState.INITIATOR, CommentState.POSTED);
    }

    @Test
    void commentFromNewDto() {
        comment.setId(null);
        Comment result = commentMapper.commentFromNewDto(event,
                user, newCommentDto, UserState.INITIATOR);
        result.setCreatedOn(comment.getCreatedOn());

        Assertions.assertEquals(comment, result);
    }

    @Test
    void shortDtoFromComment() {
        Assertions.assertEquals(commentShortDto, commentMapper.shortDtoFromComment(comment,
                userShortDto));
    }

    @Test
    void fullDtoFromComment() {
        Assertions.assertEquals(commentFullDto, commentMapper.fullDtoFromComment(comment,
                eventShortDto, userShortDto));
    }
}