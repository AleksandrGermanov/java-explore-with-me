package ru.practicum.ewmapp.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.notfound.CommentNotFoundException;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.user.dto.UserMapper;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.service.UserService;
import ru.practicum.ewmapp.util.ThrowWhen;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final EventService eventService;

    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        Event event = eventService.findEventByIdOrThrow(eventId);
        ThrowWhen.InCommentService.commentsAreNotPermitted(event);
        User commentator = userService.findUserByIdOrThrow(userId);
        UserState userState = defineUserState(commentator, event);
        Comment comment = commentMapper.commentFromNewDto(event, commentator,
                newCommentDto, userState);

        return mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> findAllCommentsForEvent(Long userId, Long eventId,
                                                         UserState userState, CommentState commentState,
                                                         Integer from, Integer size) {
        userService.throwIfUserNotExists(userId);
        eventService.throwIfEventNotExists(eventId);

        return commentRepository.findAllCommentsForEvent(eventId, userState, commentState, from, size).stream()
                .map(this::mapCommentToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> findAllCommentsForUser(Long userId, Long eventId, CommentState commentState,
                                                        CommentSortType sort, Integer from, Integer size) {
        userService.throwIfUserNotExists(userId);
        ThrowWhen.InCommentService.eventNotExistsExceptNull(eventService, eventId);
        ThrowWhen.InCommentService.sortIsCommentatorId(sort);

        return commentRepository.findAllCommentsForUser(userId, eventId, commentState, sort, from, size).stream()
                .map(this::mapCommentToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentFullDto> findAllCommentsForAdmin(Long eventId, List<Long> userIds,
                                                        UserState userState, CommentState commentState,
                                                        CommentSortType sort, Integer from, Integer size) {
        ThrowWhen.InCommentService.eventNotExistsExceptNull(eventService, eventId);
        ThrowWhen.InCommentService.userInUserIdsNotExists(userService, userIds);

        return commentRepository.findAllCommentsForAdmin(eventId, userIds, userState,
                        commentState, sort, from, size).stream()
                .map(this::mapCommentToFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentFullDto retrieveComment(Long userId, Long commentId) {
        userService.throwIfUserNotExists(userId);
        return mapCommentToFullDto(findCommentByIdOrThrow(commentId));
    }


    @Override
    @Transactional
    public CommentShortDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        userService.throwIfUserNotExists(userId);
        Comment comment = findCommentByIdOrThrow(commentId);

        ThrowWhen.InCommentService.userIsNotCommentator(userId, comment);
        ThrowWhen.InCommentService.commentStateMismatched(List.of(CommentState.POSTED, CommentState.UPDATED), comment);
        comment.setText(newCommentDto.getText());
        comment.setCommentState(CommentState.UPDATED);
        return mapCommentToShortDto(commentRepository.save(comment));
    }


    @Override
    @Transactional
    public CommentShortDto removeCommentByUser(Long userId, Long commentId) {
        userService.throwIfUserNotExists(userId);
        Comment comment = findCommentByIdOrThrow(commentId);

        ThrowWhen.InCommentService.userIsNotCommentator(userId, comment);
        ThrowWhen.InCommentService.commentStateMismatched(List.of(
                CommentState.POSTED, CommentState.UPDATED), comment);
        comment.setCommentState(CommentState.REMOVED_BY_USER);
        return mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentShortDto restoreComment(Long userId, Long commentId) {
        userService.throwIfUserNotExists(userId);
        Comment comment = findCommentByIdOrThrow(commentId);

        ThrowWhen.InCommentService.userIsNotCommentator(userId, comment);
        ThrowWhen.InCommentService.commentStateMismatched(List.of(
                CommentState.REMOVED_BY_USER), comment);
        comment.setCommentState(CommentState.UPDATED);
        return mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentShortDto moderateComment(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = findCommentByIdOrThrow(commentId);

        comment.setText(newCommentDto.getText());
        comment.setCommentState(CommentState.MODERATED);
        return mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        ThrowWhen.InCommentService.commentNotExists(commentRepository, commentId);
        commentRepository.deleteById(commentId);
    }

    private UserState defineUserState(User commentator, Event event) {
        if (commentator.equals(event.getInitiator())) {
            return UserState.INITIATOR;
        }
        if (event.getRequestsForEvent() != null && event.getRequestsForEvent().stream()
                .map(ParticipationRequest::getRequester)
                .anyMatch(commentator::equals)) {
            return UserState.REQUESTER;
        }
        return UserState.APP_USER;
    }

    private Comment findCommentByIdOrThrow(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException(String.format(
                "Comment with id = %d does not exist.", commentId)));
    }

    private CommentShortDto mapCommentToShortDto(Comment comment) {
        UserShortDto userShortDto = userMapper.userShortDtoFromUser(comment.getCommentator());
        return commentMapper.shortDtoFromComment(comment, userShortDto);
    }

    private CommentFullDto mapCommentToFullDto(Comment comment) {
        EventShortDto eventDto = eventService.mapEventShortDtoFromEvent(comment.getEvent());
        UserShortDto commentatorDto = userMapper.userShortDtoFromUser(comment.getCommentator());
        return commentMapper.fullDtoFromComment(comment, eventDto, commentatorDto);
    }
}
