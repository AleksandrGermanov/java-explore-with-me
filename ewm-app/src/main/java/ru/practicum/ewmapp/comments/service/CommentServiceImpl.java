package ru.practicum.ewmapp.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.practicum.ewmapp.exception.mismatch.CommentStateMismatchException;
import ru.practicum.ewmapp.exception.mismatch.CommentatorMismatchException;
import ru.practicum.ewmapp.exception.notfound.CommentNotFoundException;
import ru.practicum.ewmapp.exception.other.RequestParametersMisusageException;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.user.dto.UserDto;
import ru.practicum.ewmapp.user.dto.UserMapper;
import ru.practicum.ewmapp.user.dto.UserShortDto;
import ru.practicum.ewmapp.user.model.User;
import ru.practicum.ewmapp.user.service.UserService;

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
    public CommentShortDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User commentator = userService.findUserByIdOrThrow(userId);
        Event event = eventService.findEventByIdOrThrow(eventId);
        UserState userState = defineUserState(commentator, event);
        Comment comment = commentMapper.commentFromNewDto(event, commentator,
                newCommentDto, userState);
        return mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentShortDto> findAllCommentsForEvent(Long userId, Long eventId,
                                                         UserState userState, CommentState commentState,
                                                         Integer from, Integer size) {
        userService.throwIfUserNotExists(userId);
        Event event = eventService.findEventByIdOrThrow(eventId);

        return commentRepository.findAllCommentsForEvent(event, userState, commentState, from, size).stream()
                .map(this::mapCommentToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentShortDto> findAllCommentsForUser(Long userId, Long eventId, CommentState commentState,
                                                        CommentSortType sort, Integer from, Integer size) {
        User user = userService.findUserByIdOrThrow(userId);
        Event event = eventId == null ? null : eventService.findEventByIdOrThrow(eventId);

        if (CommentSortType.COMMENTATOR_ID.equals(sort)) {
            throw new RequestParametersMisusageException("CommentSortType COMMENTATOR_ID is not allowed "
                    + "for this search.");
        }
        return commentRepository.findAllCommentsForUser(user, event, commentState, sort, from, size).stream()
                .map(this::mapCommentToShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto retrieveComment(Long userId, Long commentId) {
        userService.throwIfUserNotExists(userId);
        return mapCommentToFullDto(findCommentByIdOrThrow(commentId));
    }


    @Override
    public CommentShortDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        userService.throwIfUserNotExists(userId);
        Comment comment = findCommentByIdOrThrow(commentId);

        throwIfUserIsNotCommentator(userId, comment);
        throwIfCommentStateMismatched(List.of(CommentState.POSTED, CommentState.UPDATED), comment);
        comment.setText(newCommentDto.getText());
        comment.setCommentState(CommentState.UPDATED);
        return mapCommentToShortDto(commentRepository.save(comment));
    }


    @Override
    public CommentShortDto removeCommentByUser(Long userId, Long commentId) {
        userService.throwIfUserNotExists(userId);
        Comment comment = findCommentByIdOrThrow(commentId);

        throwIfUserIsNotCommentator(userId, comment);
        throwIfCommentStateMismatched(List.of(
                CommentState.POSTED, CommentState.UPDATED), comment);
        comment.setCommentState(CommentState.REMOVED_BY_USER);
        return  mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    public CommentShortDto restoreComment(Long userId, Long commentId) {
        userService.throwIfUserNotExists(userId);
        Comment comment = findCommentByIdOrThrow(commentId);

        throwIfUserIsNotCommentator(userId, comment);
        throwIfCommentStateMismatched(List.of(
                CommentState.REMOVED_BY_USER), comment);
        comment.setCommentState(CommentState.REMOVED_BY_USER);
        return  mapCommentToShortDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentShortDto> findAllCommentsForAdmin(Long eventId, List<Long> userIds,
                                                         UserState userState, CommentState commentState,
                                                         CommentSortType sort, Integer from, Integer size) {
        Event event = eventId == null ? null
                : eventService.findEventByIdOrThrow(eventId);
        if (userIds != null) {
            userIds.forEach(userService::throwIfUserNotExists);
        }
        if (userState != null && userIds != null && !userIds.isEmpty()) {
            throw new RequestParametersMisusageException(String.format("Parameters userIds and "
                    + "userState cannot be used together. UserIds = %s, userState = %s", userIds, userState));
        }
        return commentRepository.findAllCommentsForAdmin(event, userIds, userState,
                        commentState, sort, from, size).stream()
                .map(this::mapCommentToShortDto)
                .collect(Collectors.toList());
    }



    @Override
    public CommentShortDto moderateComment(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = findCommentByIdOrThrow(commentId);
        comment.setText(newCommentDto.getText());
        comment.setCommentState(CommentState.MODERATED);
        return mapCommentToShortDto(commentRepository.save(comment));
    }


    @Override
    public void deleteCommentByAdmin(Long commentId) {
        throwIfCommentNotExists(commentId);
        commentRepository.deleteById(commentId);
    }

    private UserState defineUserState(User commentator, Event event) {
        if (commentator.equals(event.getInitiator())) {
            return UserState.INITIATOR;
        }
        if (event.getRequestsForEvent().stream()
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

    private void throwIfCommentNotExists(Long commentId) {
        if(!commentRepository.existsById(commentId)){
            throw new CommentNotFoundException(String.format(
                    "Comment with id = %d does not exist.", commentId));
        }
    }


    private void throwIfUserIsNotCommentator(Long userId, Comment comment) {
        if(userId.equals(comment.getCommentator().getId())){
            throw new CommentatorMismatchException(String.format(
                    "User is not a commentator. UserId = %d, commentId = %d",
                    userId, comment.getCommentator().getId()));
        }
    }

    private void throwIfCommentStateMismatched(List<CommentState> states, Comment comment){
        if(!states.contains(comment.getCommentState())){
            throw new CommentStateMismatchException(String.format(
                    "Required operation is not supported for comment with given commentState. CommentId = %d, " +
                            "commentState = %s.", comment.getId(), comment.getCommentState()));
        }
    }

    private CommentShortDto mapCommentToShortDto(Comment comment) {
        UserShortDto userShortDto = userMapper.userShortDtoFromUser(comment.getCommentator());
        return commentMapper.shortDtoFromComment(comment, userShortDto);
    }

    private CommentFullDto mapCommentToFullDto(Comment comment) {
        EventShortDto eventDto  = eventService.mapEventShortDtoFromEvent(comment.getEvent());
        UserDto commentatorDto = userMapper.userDtoFromUser(comment.getCommentator());
        return commentMapper.fullDtoFromComment(comment, eventDto, commentatorDto);
    }
}
