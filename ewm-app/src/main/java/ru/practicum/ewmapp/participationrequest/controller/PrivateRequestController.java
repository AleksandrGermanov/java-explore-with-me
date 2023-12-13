package ru.practicum.ewmapp.participationrequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;
import ru.practicum.ewmapp.participationrequest.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public List<ParticipationRequestDto> findAllByRequesterId(@PathVariable(name = "userId") Long requesterId) {
        return participationRequestService.findAllByRequesterId(requesterId);
    }

    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable(name = "userId") Long requesterId,
                                                 @RequestParam Long eventId) {
        return participationRequestService.createRequest(requesterId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
