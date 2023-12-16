package ru.practicum.ewmapp.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.moderation.UpdateEventAdminRequest;
import ru.practicum.ewmapp.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    List<EventFullDto> findAllForAdmin(@RequestParam(required = false, name = "users") List<Long> userIds,
                                       @RequestParam(required = false) List<EventState> states,
                                       @RequestParam(required = false) LocalDateTime rangeStart,
                                       @RequestParam(required = false) LocalDateTime rangeEnd,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Processing incoming request GET /admin/events. UserIds = {}, states = {}, "
                        + "rangeStart = {}, rangeEnd = {}, from = {}, size = {}", userIds, states, rangeStart,
                rangeEnd, from, size);
        return eventService.findAllEventsForAdmin(userIds, states, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    EventFullDto updateEventByAdminRequest(@PathVariable Long eventId,
                                           @RequestBody UpdateEventAdminRequest adminRequest) {
        log.info("Processing incoming request PATCH /admin/events/{}. AdminRequest = {}", eventId, adminRequest);
        return eventService.updateEventByAdminRequest(eventId, adminRequest);
    }
}
