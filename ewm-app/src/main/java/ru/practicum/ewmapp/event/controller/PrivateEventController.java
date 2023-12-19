package ru.practicum.ewmapp.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.dto.NewCommentDto;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.service.CommentService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public List<EventShortDto> findAllByUser(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Processing incoming request GET /users/{}/events. From = {}, size = {}.", userId, from, size);
        return eventService.findAllByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventDto dto) {
        log.info("Processing incoming request POST /users/{}/events. Dto = {}.", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findByUserAndById(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("Processing incoming request GET /users/{}/events/{}.", userId, eventId);
        return eventService.findByUserAndById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserRequest(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody UpdateEventUserRequest request) {
        log.info("Processing incoming request PATCH /users/{}/events/{}. Request = {}.", userId, eventId, request);
        return eventService.updateEventByUserRequest(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findAllRequestsForEvent(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        log.info("Processing incoming request GET /users/{}/events/{}/requests.", userId, eventId);
        return eventService.findAllRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusForEventRequests(@PathVariable Long userId,
                                                                       @PathVariable Long eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest
                                                                               updateRequest) {
        log.info("Processing incoming request PATCH /users/{}/events/{}/requests. UpdateRequest = {}.",
                userId, eventId, updateRequest);
        return eventService.updateStatusForEventRequests(userId, eventId, updateRequest);
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Processing incoming request POST /users/{}/events/{}/comments. NewCommentDto = {}.",
                userId, eventId, newCommentDto);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentShortDto> findAllCommentsForEvent(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestParam(required = false) UserState userState,
                                                         @RequestParam(required = false) CommentState commentState,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Processing incoming request GET /users/{}/events/{}/comments. UserState = {}, commentState = {},"
                        + "from = {}, size = {}.",
                userId, eventId, userState, commentState, from, size);
        return commentService.findAllCommentsForEvent(userId, eventId, userState, commentState, from, size);
    }
}