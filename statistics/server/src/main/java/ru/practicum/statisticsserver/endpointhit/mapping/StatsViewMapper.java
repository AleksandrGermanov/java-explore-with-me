package ru.practicum.statisticsserver.endpointhit.mapping;

import org.springframework.stereotype.Component;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

@Component
public class StatsViewMapper {
    public StatsView statsViewFromDto(StatsViewDto dto) {
        return new StatsView(dto.getApp(), dto.getUri(), dto.getCount());
    }

    public StatsViewDto statsViewToDto(StatsView view) {
        return new StatsViewDto(view.getApp(), view.getUri(), view.getCount());
    }
}
