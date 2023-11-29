package ru.practicum.statisticsserver.endpointhit.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;

import java.time.LocalDateTime;

public class EndpointHitMapperTest {
    private EndpointHitMapper mapper = new EndpointHitMapper();
    private EndpointHit hit;
    private EndpointHitDto dto;
    private EndpointHit nullHit;
    private EndpointHitDto nullDto;

    @BeforeEach
    public void setup() {
        hit = new EndpointHit(1L, "app", "/uri", "0.0.0.0",
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        dto = new EndpointHitDto(1L, "app", "/uri", "0.0.0.0",
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        nullHit = new EndpointHit(null, null, null, null, null);
        nullDto = new EndpointHitDto(null, null, null, null, null);
    }

    @Test
    public void endpointHitFromDtoTest() {
        Assertions.assertEquals(hit, mapper.endpointHitFromDto(dto));
    }

    @Test
    public void endpointHitFromDtoNullValuesTest() {
        Assertions.assertEquals(nullHit, mapper.endpointHitFromDto(nullDto));
    }

    @Test
    public void endpointHitToDtoTest() {
        Assertions.assertEquals(dto, mapper.endpointHitToDto(hit));
    }

    @Test
    public void endpointHitToDtoNullValuesTest() {
        Assertions.assertEquals(nullDto, mapper.endpointHitToDto(nullHit));
    }
}
