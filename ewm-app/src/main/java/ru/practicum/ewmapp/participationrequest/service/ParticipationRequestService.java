package ru.practicum.ewmapp.participationrequest.service;

import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequest> findAllByEvent(Event event);

    List<ParticipationRequestDto> findAllByRequesterId(Long requesterId);

    ParticipationRequestDto createRequest(Long requesterId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
