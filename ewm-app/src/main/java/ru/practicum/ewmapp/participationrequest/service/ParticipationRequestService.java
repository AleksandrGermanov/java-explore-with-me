package ru.practicum.ewmapp.participationrequest.service;

import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequest> findAllByEvent(Event event);
}
