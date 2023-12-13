package ru.practicum.endpointhitclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.lang.Nullable;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitClient {
    EndpointHitDto saveEndpointHit(String uri, String ip, LocalDateTime timestamp);

    List<StatsViewDto> retrieveStatsViewList(LocalDateTime start, LocalDateTime end,
                                             @Nullable List<String> uris, boolean unique);
}
