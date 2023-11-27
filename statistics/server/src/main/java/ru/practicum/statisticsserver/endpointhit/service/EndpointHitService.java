package ru.practicum.statisticsserver.endpointhit.service;

import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {
    @Transactional(readOnly = true)
    List<StatsViewDto> retrieveStatsViewList(LocalDateTime start, LocalDateTime end,
                                             @Nullable List<String> uris, boolean unique);

    @Transactional
    EndpointHitDto saveHit(EndpointHitDto dto);
}
