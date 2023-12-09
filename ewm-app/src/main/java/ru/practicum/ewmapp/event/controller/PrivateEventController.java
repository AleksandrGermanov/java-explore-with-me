package ru.practicum.ewmapp.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.event.dto.EventFullDto;
import ru.practicum.ewmapp.event.dto.EventShortDto;
import ru.practicum.ewmapp.event.dto.NewEventDto;
import ru.practicum.ewmapp.event.moderation.UpdateEventUserRequest;
import ru.practicum.ewmapp.event.service.EventService;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateRequest;
import ru.practicum.ewmapp.participationrequest.moderation.EventRequestStatusUpdateResult;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findAllByUser(@PathVariable Long userId,
                                             @RequestParam @PositiveOrZero @DefaultValue(value = "0") Integer from,
                                             @RequestParam @Positive @DefaultValue(value = "10") Integer size){
        return eventService.findAllByUser(userId, from, size);
    }

    @PostMapping
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventDto dto){
        return eventService.createEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findByUserAndById(@PathVariable Long userId,
                                          @PathVariable Long eventId){
        return eventService.findByUserAndById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserRequest(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody UpdateEventUserRequest request){
        return eventService.updateEventByUserRequest(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findAllRequestsForEvent(@PathVariable Long userId,
                                                                 @PathVariable Long eventId){
        return eventService.findAllRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusForEventRequests(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest updateRequest){
        return eventService.updateStatusForEventRequests(userId, eventId, updateRequest);
    }
}