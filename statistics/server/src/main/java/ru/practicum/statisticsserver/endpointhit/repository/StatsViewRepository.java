package ru.practicum.statisticsserver.endpointhit.repository;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsViewRepository {
    @Transactional(readOnly = true)
    List<StatsView> getViewListForAllUrisNotUniqueIp(LocalDateTime start, LocalDateTime end);

    @Transactional(readOnly = true)
    List<StatsView> getViewListForAllUrisUniqueIp(LocalDateTime start, LocalDateTime end);

    @Transactional(readOnly = true)
    List<StatsView> getViewListForSelectedUrisNotUniqueIp(LocalDateTime start, LocalDateTime end,
                                                          List<String> selectedUris);

    @Transactional(readOnly = true)
    List<StatsView> getViewListForSelectedUrisUniqueIp(LocalDateTime start, LocalDateTime end,
                                                       List<String> selectedUris);
}
