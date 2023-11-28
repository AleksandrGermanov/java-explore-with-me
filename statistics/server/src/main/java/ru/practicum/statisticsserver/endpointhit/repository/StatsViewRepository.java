package ru.practicum.statisticsserver.endpointhit.repository;

import ru.practicum.statisticsserver.endpointhit.model.StatsView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsViewRepository {
    List<StatsView> getViewListForAllUrisNotUniqueIp(LocalDateTime start, LocalDateTime end);

    List<StatsView> getViewListForAllUrisUniqueIp(LocalDateTime start, LocalDateTime end);

    List<StatsView> getViewListForSelectedUrisNotUniqueIp(LocalDateTime start, LocalDateTime end,
                                                          List<String> selectedUris);

    List<StatsView> getViewListForSelectedUrisUniqueIp(LocalDateTime start, LocalDateTime end,
                                                       List<String> selectedUris);
}
