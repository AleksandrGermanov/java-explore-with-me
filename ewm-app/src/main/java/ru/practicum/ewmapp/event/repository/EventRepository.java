package ru.practicum.ewmapp.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {
    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);
}
