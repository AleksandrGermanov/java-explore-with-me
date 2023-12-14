package ru.practicum.ewmapp.participationrequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public List<ParticipationRequestDto> findAllByRequesterId(@PathVariable(name = "userId") Long requesterId) {
        log.info("Processing incoming request GET /users/{}/requests.", requesterId);
        return participationRequestService.findAllByRequesterId(requesterId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(name = "userId") Long requesterId,
                                                 @RequestParam Long eventId) {
        log.info("Processing incoming request POST /users/{}/requests. EventId = {}.", requesterId, eventId);
        return participationRequestService.createRequest(requesterId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Processing incoming request PATCH /users/{}/requests/{}/cancel.", userId, requestId);
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
