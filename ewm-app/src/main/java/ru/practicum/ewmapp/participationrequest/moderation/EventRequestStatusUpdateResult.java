package ru.practicum.ewmapp.participationrequest.moderation;

import lombok.Data;
import ru.practicum.ewmapp.participationrequest.dto.ParticipationRequestDto;

import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
