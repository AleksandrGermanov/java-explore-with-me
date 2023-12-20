package ru.practicum.ewmapp.participationrequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.exception.notfound.RequestNotFoundException;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestMapper;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;
import ru.practicum.ewmapp.user.service.UserService;
import ru.practicum.ewmapp.util.ThrowWhen;

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
    @Transactional(readOnly = true)
    public List<ParticipationRequest> findAllByEvent(Event event) {
        return participationRequestRepository.findAllByEvent(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllByRequesterId(Long requesterId) {
        return participationRequestRepository.findAllByRequesterId(requesterId).stream()
                .map(participationRequestMapper::dtoFromParticipationRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long requesterId, Long eventId) {
        Event event = eventService.findEventByIdOrThrow(eventId);

        ThrowWhen.InParticipationRequestService.requesterIsEventInitiator(event, requesterId);
        ThrowWhen.InParticipationRequestService.requestAlreadyExist(participationRequestRepository,
                event, requesterId);
        ThrowWhen.InParticipationRequestService.eventIsNotPublished(event);
        ThrowWhen.InParticipationRequestService.participantLimitOfEventIsReached(event);

        ParticipationRequest request = formNewParticipationRequest(event, requesterId);
        return participationRequestMapper.dtoFromParticipationRequest(participationRequestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = findRequestByIdOrThrow(requestId);
        ThrowWhen.InParticipationRequestService.userIsNotRequester(request, userId);
        request.setStatus(ParticipationRequestStatus.CANCELED);
        return participationRequestMapper.dtoFromParticipationRequest(participationRequestRepository.save(request));
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
        ParticipationRequestStatus status = (!event.getRequestModeration() || event.getParticipantLimit() == 0)
                ? ParticipationRequestStatus.CONFIRMED
                : ParticipationRequestStatus.PENDING;
        request.setStatus(status);
        return request;
    }
}
