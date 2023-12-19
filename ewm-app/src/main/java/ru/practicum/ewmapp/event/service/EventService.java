package ru.practicum.ewmapp.event.service;

import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.moderation.UpdateEventAdminRequest;
import ru.practicum.ewmapp.event.moderation.UpdateEventUserRequest;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateRequest;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateResult;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> findAllByUser(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto dto);

    EventFullDto findByUserAndById(Long userId, Long eventId);

    EventFullDto updateEventByUserRequest(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> findAllRequestsForEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusForEventRequests(
            Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<EventFullDto> findAllEventsForAdmin(List<Long> userIds, List<EventState> states,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from, Integer size);

    EventFullDto updateEventByAdminRequest(Long eventId, UpdateEventAdminRequest adminRequest);

    List<EventShortDto> findAllEventsForUser(String text, List<Long> categoryIds, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, PublicEventSortType sort,
                                             Integer from, Integer size, String uri, String remoteIp);

    EventFullDto retrievePublishedEvent(Long eventId, String uri, String remoteIp);

    Event findEventByIdOrThrow(Long eventId);

    EventShortDto mapEventShortDtoFromEvent(Event event);

    void throwIfEventNotExists(Long eventId);
}
