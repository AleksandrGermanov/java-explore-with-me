package ru.practicum.ewmapp.participationrequest.dto;

import org.springframework.stereotype.Service;
import ru.practicum.ewmapp.participationrequest.model.ParticipationRequest;

@Service
public class ParticipationRequestMapperImpl implements ParticipationRequestMapper {

    @Override
    public ParticipationRequestDto dtoFromParticipationRequest(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(participationRequest.getId(), participationRequest.getCreated(),
                participationRequest.getEvent().getId(), participationRequest.getRequester().getId(),
                participationRequest.getStatus());
    }
}
