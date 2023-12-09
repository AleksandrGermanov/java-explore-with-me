package ru.practicum.ewmapp.participationrequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;
import ru.practicum.ewmapp.participationrequest.repository.ParticipationRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService{
    private final ParticipationRequestRepository participationRequestRepository;
    @Override
    public List<ParticipationRequest> findAllByEvent(Event event) {
        return participationRequestRepository.findAllByEvent(event);
    }
}
