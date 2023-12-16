package ru.practicum.ewmapp.event.repository;

import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.service.PublicEventSortType;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomEventRepository {
    List<Event> findAllEventsForAdmin(List<Long> userIds, List<EventState> states,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from, Integer size);

    List<Event> findAllEventsForUser(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Boolean onlyAvailable, PublicEventSortType sort,
                                     Integer from, Integer size);
}
