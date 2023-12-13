package ru.practicum.ewmapp.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {
    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    Optional<Event> findEventByIdAndState(long eventId, EventState state);
}
