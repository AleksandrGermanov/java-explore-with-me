package ru.practicum.statisticsserver.endpointhit.mapping;

import org.springframework.stereotype.Component;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;

@Component
public class EndpointHitMapper {
    public EndpointHit endpointHitFromDto(EndpointHitDto dto) {
        return new EndpointHit(dto.getId(), dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
    }

    public EndpointHitDto endpointHitToDto(EndpointHit hit) {
        return new EndpointHitDto(hit.getId(), hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }
}
