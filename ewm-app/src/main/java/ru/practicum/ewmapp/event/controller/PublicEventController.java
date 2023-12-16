package ru.practicum.ewmapp.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.event.service.PublicEventSortType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findAllForUser(@RequestParam(required = false) String text,
                                              @RequestParam(required = false, name = "categories") List<Long> categoryIds,
                                              @RequestParam(required = false) Boolean paid,
                                              @RequestParam(required = false) LocalDateTime rangeStart,
                                              @RequestParam(required = false) LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                              @RequestParam(required = false) PublicEventSortType sort,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size,
                                              HttpServletRequest request) {
        log.info("Processing incoming request GET /events. Text = {}, categoryIds = {}, paid = {}, states = {}, "
                        + "rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findAllEventsForUser(text, categoryIds, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request.getRequestURI(), request.getRemoteAddr());
    }

    @GetMapping("/{eventId}")
    public EventFullDto retrievePublishedEvent(@PathVariable Long eventId,
                                               HttpServletRequest request) {
        log.info("Processing incoming request GET /events/{}. Fetched uri = {}, fetched remote address = {}.",
                eventId, request.getRequestURI(), request.getRemoteAddr());
        return eventService.retrievePublishedEvent(eventId, request.getRequestURI(), request.getRemoteAddr());
    }
}