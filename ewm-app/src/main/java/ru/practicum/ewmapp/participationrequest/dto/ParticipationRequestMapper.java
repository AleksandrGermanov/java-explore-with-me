package ru.practicum.ewmapp.participationrequest.dto;

import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;

public interface ParticipationRequestMapper {
    ParticipationRequestDto dtoFromParticipationRequest(ParticipationRequest participationRequest);
}
