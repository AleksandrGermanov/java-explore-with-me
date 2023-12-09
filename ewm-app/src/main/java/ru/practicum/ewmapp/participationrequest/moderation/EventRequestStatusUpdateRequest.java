package ru.practicum.ewmapp.participationrequest.moderation;

import lombok.Data;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private ParticipationRequestStatus status;
}
