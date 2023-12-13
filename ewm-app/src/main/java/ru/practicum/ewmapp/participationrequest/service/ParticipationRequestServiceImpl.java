package ru.practicum.ewmapp.participationrequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.exception.other.ParticipantLimitReachedException;
import ru.practicum.ewmapp.exception.other.RequestAlreadyExistsException;
import ru.practicum.ewmapp.exception.mismatch.EventStateMismatchException;
import ru.practicum.ewmapp.exception.mismatch.RequesterMismatchException;
import ru.practicum.ewmapp.exception.notfound.RequestNotFoundException;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestMapper;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;
import ru.practicum.ewmapp.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public List<ParticipationRequest> findAllByEvent(Event event) {
        return participationRequestRepository.findAllByEvent(event);
    }

    @Override
    public List<ParticipationRequestDto> findAllByRequesterId(Long requesterId) {
        return participationRequestRepository.findAllByRequesterId(requesterId).stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long requesterId, Long eventId) {
        Event event = eventService.findEventByIdOrThrow(eventId);
        throwIfRequesterIsEventInitiator(event, requesterId);
        throwIfRequestAlreadyExist(event, requesterId);
        throwIfEventIsNotPublished(event);
        throwIfParticipantLimitOfEventIsReached(event);

        ParticipationRequest request = formNewParticipationRequest(event, requesterId);
        return participationRequestMapper.dtoFromParticipationRequest(participationRequestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = findRequestByIdOrThrow(requestId);
        throwIfUserIsNotRequester(request, userId);
        request.setStatus(ParticipationRequestStatus.CANCELLED);
        return participationRequestMapper.dtoFromParticipationRequest(participationRequestRepository.save(request));
    }

    private void throwIfUserIsNotRequester(ParticipationRequest request, Long userId) {
        if (!request.getRequester().getId().equals(userId)) {
            throw new RequesterMismatchException(String.format("Request can be cancelled only by requester."
                    + " Request id = %d,  user id = %d", request.getId(), userId));
        }
    }

    private ParticipationRequest findRequestByIdOrThrow(Long requestId) {
        return participationRequestRepository.findById(requestId).orElseThrow(() ->
                new RequestNotFoundException(String.format("Request with id = %d does not exist", requestId)));
    }

    private ParticipationRequest formNewParticipationRequest(Event event, Long requesterId) {
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setRequester(userService.findUserByIdOrThrow(requesterId));
        request.setEvent(event);
        ParticipationRequestStatus status = event.getRequestModeration() ? ParticipationRequestStatus.PENDING
                : ParticipationRequestStatus.CONFIRMED;
        request.setStatus(status);
        return request;
    }

    private void throwIfParticipantLimitOfEventIsReached(Event event) {
        if (event.getParticipantLimit().equals(event.getConfirmedRequests().size())) {
            throw new ParticipantLimitReachedException(String.format("Participant limit for this event "
                    + "has been reached. Event id = %d", event.getId()));
        }
    }

    private void throwIfEventIsNotPublished(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateMismatchException(String.format("Request can be created for published events only."
                    + " Event id = %d", event.getId()));
        }
    }

    private void throwIfRequestAlreadyExist(Event event, Long requesterId) {
        if (participationRequestRepository.findByEventAndRequesterId(event, requesterId).isPresent()) {
            throw new RequestAlreadyExistsException(String.format("Request for this event " +
                            "has already been created by the user. Event id = %d, user id = %d.",
                    event.getId(), requesterId));
        }
    }

    private void throwIfRequesterIsEventInitiator(Event event, Long requesterId) {
        if (event.getInitiator().getId().equals(requesterId)) {
            throw new RequesterMismatchException(String.format("Requester and event initiator have the same id."
                    + "Event id= %d, requester id = %d.", event.getId(), requesterId));
        }
    }
}
