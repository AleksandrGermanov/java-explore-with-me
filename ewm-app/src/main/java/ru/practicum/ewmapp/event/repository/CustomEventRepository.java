package ru.practicum.ewmapp.event.repository;

import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomEventRepository {
    List<Event> findAllEventsForAdmin(List<Long> userIds, List<EventState> states,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from, Integer size);
}
