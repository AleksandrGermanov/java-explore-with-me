package ru.practicum.ewmapp.comments.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmapp.category.dto.CategoryDto;
import ru.practicum.ewmapp.category.model.Category;
import ru.practicum.ewmapp.comments.dto.CommentFullDto;
import ru.practicum.ewmapp.comments.dto.CommentMapper;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.dto.NewCommentDto;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.repository.CommentRepository;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.model.Location;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.mismatch.CommentStateMismatchException;
import ru.practicum.ewmapp.exception.mismatch.CommentatorMismatchException;
import ru.practicum.ewmapp.exception.notfound.CommentNotFoundException;
import ru.practicum.ewmapp.exception.other.RequestParametersMisusageException;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.user.dto.UserMapper;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EventService eventService;

    private User user;
    private UserShortDto userShortDto;
    private Event event;
    private EventShortDto eventShortDto;
    private ParticipationRequest request1;
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
        request1 = new ParticipationRequest(
                1L, null, event, new User(999L, "", ""),
                ParticipationRequestStatus.PENDING);
        comment = new Comment(0L, publishedOn, event, user,
                "text", UserState.INITIATOR, CommentState.POSTED);
        newCommentDto = new NewCommentDto("text");
        commentShortDto = new CommentShortDto(0L, publishedOn, 0L, userShortDto, "text",
                UserState.INITIATOR, CommentState.POSTED);
        commentFullDto = new CommentFullDto(0L, publishedOn, eventShortDto,
                userShortDto, "text", UserState.INITIATOR, CommentState.POSTED);
    }

    @Test
    void createCommentReturnsSetsUserState() {
        event.setInitiator(user);

        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(userService.findUserByIdOrThrow(0L))
                .thenReturn(user);
        when(commentMapper.commentFromNewDto(event, user, newCommentDto, UserState.INITIATOR))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto, commentService.createComment(0L, 0L, newCommentDto));
    }

    @Test
    void createCommentReturnsSetsUserStateAPP_USER() {
        User userr = new User(999L, "null", "null");
        event.setInitiator(userr);
        comment.setUserState(UserState.APP_USER);
        commentShortDto.setUserState(UserState.APP_USER);

        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(userService.findUserByIdOrThrow(0L))
                .thenReturn(user);
        when(commentMapper.commentFromNewDto(event, user, newCommentDto, UserState.APP_USER))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto, commentService.createComment(0L, 0L, newCommentDto));
    }

    @Test
    void createCommentReturnsSetsUserStateREQUESTER() {
        User requester = new User(999L, "null", "null");
        request1.setRequester(requester);
        event.setRequestsForEvent(List.of(request1));
        comment.setCommentator(requester);
        comment.setUserState(UserState.REQUESTER);
        commentShortDto.setUserState(UserState.REQUESTER);

        when(eventService.findEventByIdOrThrow(0L))
                .thenReturn(event);
        when(userService.findUserByIdOrThrow(999L))
                .thenReturn(requester);
        when(commentMapper.commentFromNewDto(event, requester, newCommentDto, UserState.REQUESTER))
                .thenReturn(comment);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(requester))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto, commentService.createComment(999L, 0L, newCommentDto));
    }

    @Test
    void findAllCommentsForEventReturnsValue() {
        when(commentRepository.findAllCommentsForEvent(0L, UserState.APP_USER,
                CommentState.POSTED, 0, 10))
                .thenReturn(List.of(comment));
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(List.of(commentShortDto),
                commentService.findAllCommentsForEvent(0L, 0L, UserState.APP_USER,
                        CommentState.POSTED, 0, 10));
    }

    @Test
    void findAllCommentsForUserReturnsValue() {
        when(commentRepository.findAllCommentsForUser(0L, 0L,
                CommentState.POSTED, CommentSortType.DATE_DESC, 0, 10))
                .thenReturn(List.of(comment));
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(List.of(commentShortDto),
                commentService.findAllCommentsForUser(0L, 0L,
                        CommentState.POSTED, CommentSortType.DATE_DESC, 0, 10));
    }

    @Test
    void findAllCommentsForUserWhenSortIsCOMMENTATOR_IDThrows() {
        Assertions.assertThrows(RequestParametersMisusageException.class,
                () -> commentService.findAllCommentsForUser(0L, 0L,
                        CommentState.POSTED, CommentSortType.COMMENTATOR_ID, 0, 10));
    }

    @Test
    void findAllCommentsForAdmin() {
        when(commentRepository.findAllCommentsForAdmin(0L, null, UserState.APP_USER,
                CommentState.POSTED, CommentSortType.DATE_DESC, 0, 10))
                .thenReturn(List.of(comment));
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(commentMapper.fullDtoFromComment(comment, eventShortDto, userShortDto))
                .thenReturn(commentFullDto);

        Assertions.assertEquals(List.of(commentFullDto),
                commentService.findAllCommentsForAdmin(0L, null, UserState.APP_USER,
                        CommentState.POSTED, CommentSortType.DATE_DESC, 0, 10));
    }

    @Test
    void retrieveCommentReturnsValue() {
        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(eventService.mapEventShortDtoFromEvent(event))
                .thenReturn(eventShortDto);
        when(commentMapper.fullDtoFromComment(comment, eventShortDto, userShortDto))
                .thenReturn(commentFullDto);

        Assertions.assertEquals(commentFullDto,
                commentService.retrieveComment(0L, 0L));
    }

    @Test
    void retrieveCommentThrowsIfNotFound() {
        when(commentRepository.findById(0L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CommentNotFoundException.class,
                () -> commentService.retrieveComment(0L, 0L));
    }

    @Test
    void updateCommentReturnsValue() {
        newCommentDto.setText("TEXT");

        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto,
                commentService.updateComment(0L, 0L, newCommentDto));
        Assertions.assertEquals("TEXT", comment.getText());
        Assertions.assertEquals(CommentState.UPDATED, comment.getCommentState());
    }

    @Test
    void updateCommentWhenUserIsNotCommentatorThrows() {
        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));

        Assertions.assertThrows(CommentatorMismatchException.class,
                () -> commentService.updateComment(1L, 0L, newCommentDto));
    }

    @Test
    void updateCommentWhenCommentHasWrongStateThrows() {
        comment.setCommentState(CommentState.MODERATED);

        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));

        Assertions.assertThrows(CommentStateMismatchException.class,
                () -> commentService.updateComment(0L, 0L, newCommentDto));
    }

    @Test
    void removeCommentByUserReturnsValue() {
        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto,
                commentService.removeCommentByUser(0L, 0L));
        Assertions.assertEquals(CommentState.REMOVED_BY_USER, comment.getCommentState());
    }

    @Test
    public void restoreComment() {
        comment.setCommentState(CommentState.REMOVED_BY_USER);

        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto,
                commentService.restoreComment(0L, 0L));
        Assertions.assertEquals(CommentState.UPDATED, comment.getCommentState());
    }

    @Test
    void moderateComment() {
        newCommentDto.setText("TEXT");

        when(commentRepository.findById(0L))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(userMapper.userShortDtoFromUser(user))
                .thenReturn(userShortDto);
        when(commentMapper.shortDtoFromComment(comment, userShortDto))
                .thenReturn(commentShortDto);

        Assertions.assertEquals(commentShortDto,
                commentService.moderateComment(0L, newCommentDto));
        Assertions.assertEquals("TEXT", comment.getText());
        Assertions.assertEquals(CommentState.MODERATED, comment.getCommentState());
    }

    @Test
    void deleteCommentByAdmin() {
        when(commentRepository.existsById(0L))
                .thenReturn(true);

        commentService.deleteCommentByAdmin(0L);

        verify(commentRepository, times(1)).deleteById(0L);
    }
}