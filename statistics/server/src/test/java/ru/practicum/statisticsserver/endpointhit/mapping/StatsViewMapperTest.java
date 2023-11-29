package ru.practicum.statisticsserver.endpointhit.mapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

import java.time.LocalDateTime;

public class StatsViewMapperTest {
    private StatsViewMapper mapper = new StatsViewMapper();
    private StatsView view;
    private StatsViewDto dto;
    private StatsView nullView;
    private StatsViewDto nullViewDto;

    @BeforeEach
    public void setup() {
        view = new StatsView("app", "/uri", 1L);
        LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        dto = new StatsViewDto("app", "/uri", 1L);
        nullView = new StatsView(null, null, null);
        nullViewDto = new StatsViewDto(null, null, null);
    }

    @Test
    public void statsViewFromDtoTest() {
        Assertions.assertEquals(view, mapper.statsViewFromDto(dto));
    }

    @Test
    public void statsViewFromDtoNullValuesTest() {
        Assertions.assertEquals(nullView, mapper.statsViewFromDto(nullViewDto));
    }

    @Test
    public void statsViewToDtoTest() {
        Assertions.assertEquals(dto, mapper.statsViewToDto(view));
    }

    @Test
    public void statsViewToDtoNullValuesTest() {
        Assertions.assertEquals(nullViewDto, mapper.statsViewToDto(nullView));
    }
}
