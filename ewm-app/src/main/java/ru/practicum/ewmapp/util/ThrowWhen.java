package ru.practicum.ewmapp.util;

import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.repository.CommentRepository;
import ru.practicum.ewmapp.comments.service.CommentSortType;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.mismatch.*;
import ru.practicum.ewmapp.exception.notfound.CommentNotFoundException;
import ru.practicum.ewmapp.exception.other.*;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateRequest;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;
import ru.practicum.ewmapp.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ThrowWhen {

    public abstract static class InCommentService {

        public static void commentNotExists(CommentRepository commentRepository, Long commentId) {
            if (!commentRepository.existsById(commentId)) {
                throw new CommentNotFoundException(String.format(
                        "Comment with id = %d does not exist.", commentId));
            }
        }

        public static void eventNotExistsExceptNull(EventService eventService, Long eventId) {
            if (eventId != null) {
                eventService.throwIfEventNotExists(eventId);
            }
        }

        public static void userIsNotCommentator(Long userId, Comment comment) {
            if (!userId.equals(comment.getCommentator().getId())) {
                throw new CommentatorMismatchException(String.format(
                        "User is not a commentator. UserId = %d, commentId = %d",
                        userId, comment.getId()));
            }
        }

        public static void commentStateMismatched(List<CommentState> states, Comment comment) {
            if (!states.contains(comment.getCommentState())) {
                throw new CommentStateMismatchException(String.format(
                        "Required operation is not supported for comment with given commentState. CommentId = %d, " +
                                "commentState = %s.", comment.getId(), comment.getCommentState()));
            }
        }

        public static void commentsAreNotPermitted(Event event) {
            if (!event.getPermitComments()) {
                throw new CommentsAreNotAllowedException(String.format("Comments are not allowed for this event. "
                        + "EventId = %d", event.getId()));
            }
        }

        public static void userInUserIdsNotExists(UserService userService, List<Long> userIds) {
            if (userIds != null) {
                userIds.forEach(userService::throwIfUserNotExists);
            }
        }

        public static void sortIsCommentatorId(CommentSortType sort) {
            if (CommentSortType.COMMENTATOR_ID.equals(sort)) {
                throw new RequestParametersMisusageException("CommentSortType COMMENTATOR_ID is not allowed "
                        + "for this search.");
            }
        }
    }

    public abstract static class InEventService {

        public static void requestCapacityIsNotEnough(EventRequestStatusUpdateRequest updateRequest, Event event) {
            throw new EventRemainingCapacityMismatchException(String.format("Not enough "
                            + "capacity left for confirming all requests. EventId = %d, ParticipationLimit = %d, "
                            + "ConfirmedRequestsSize = %d, RequestIdsSize = %d", event.getId(),
                    event.getParticipantLimit(), event.getConfirmedRequests().size(),
                    updateRequest.getRequestIds().size()));
        }

        public static void eventRequestNotRequiresModeration(Event event) {
            if (event.getRequestModeration().equals(false) || event.getParticipantLimit() == 0) {
                throw new ModerationNotRequiredException(
                        String.format("For this event moderation is not required. EventId = %d", event.getId())
                );
            }
        }

        public static void startIsAfterEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
            if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
                throw new StartIsAfterEndException(String.format("RangeStart cannot be after rangeEnd. "
                        + "RangeStart = %s, rangeEnd = %s.", rangeStart, rangeEnd));
            }
        }

        public static void eventInitiatorIdAndUserIdDiffer(Event event, Long userId) {
            if (!event.getInitiator().getId().equals(userId)) {
                throw new InitiatorMismatchException(String.format("Requested event has another initiator." +
                        " EventId = %d, userId = %d", event.getId(), userId));
            }
        }

        public static void requestStatusIsNotPendingForStatusUpdate(EventRequestStatusUpdateRequest updateRequest,
                                                                    List<ParticipationRequest> pendingRequests) {
            Set<Long> pendingRequestsIds = pendingRequests.stream()
                    .map(ParticipationRequest::getId)
                    .collect(Collectors.toSet());
            updateRequest.getRequestIds()
                    .forEach(id -> {
                        if (!pendingRequestsIds.contains(id)) {
                            throw new RequestIdMismatchException(String.format("Status can be changed only for"
                                    + " requests with PENDING status. Request with id = %d may be with "
                                    + "the other status.", id));
                        }
                    });
        }
    }

    public abstract static class InParticipationRequestService {

        public static void participantLimitOfEventIsReached(Event event) {
            if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests().size())) {
                throw new ParticipantLimitReachedException(String.format("Participant limit for this event "
                        + "has been reached. Event id = %d", event.getId()));
            }
        }

        public static void eventIsNotPublished(Event event) {
            if (!event.getState().equals(EventState.PUBLISHED)) {
                throw new EventStateMismatchException(String.format("Request can be created for published events only."
                        + " Event id = %d", event.getId()));
            }
        }

        public static void requestAlreadyExist(ParticipationRequestRepository participationRequestRepository,
                                               Event event, Long requesterId) {
            if (participationRequestRepository.findByEventAndRequesterId(event, requesterId).isPresent()) {
                throw new RequestAlreadyExistsException(String.format("Request for this event " +
                                "has already been created by the user. Event id = %d, user id = %d.",
                        event.getId(), requesterId));
            }
        }

        public static void requesterIsEventInitiator(Event event, Long requesterId) {
            if (event.getInitiator().getId().equals(requesterId)) {
                throw new RequesterMismatchException(String.format("Requester and event initiator have the same id."
                        + "Event id= %d, requester id = %d.", event.getId(), requesterId));
            }
        }


        public static void userIsNotRequester(ParticipationRequest request, Long userId) {
            if (!request.getRequester().getId().equals(userId)) {
                throw new RequesterMismatchException(String.format("Request can be cancelled only by requester."
                        + " Request id = %d,  user id = %d", request.getId(), userId));
            }
        }
    }
}

