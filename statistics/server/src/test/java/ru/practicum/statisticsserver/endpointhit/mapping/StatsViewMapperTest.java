package ru.practicum.statisticsserver.endpointhit.mapping;

import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

public class StatsViewMapperTest {
    private StatsViewMapper mapper = new StatsViewMapper();
    private StatsView view = new StatsView("app", "/uri", 1L);
    private StatsViewDto dto;
    private StatsView nullView;
    private StatsViewDto nullViewDto;

}
