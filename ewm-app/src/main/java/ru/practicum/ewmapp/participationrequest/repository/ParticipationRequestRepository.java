package ru.practicum.ewmapp.participationrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEvent(Event event);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    Optional<ParticipationRequest> findByEventAndRequesterId(Event event, Long requesterId);
}
