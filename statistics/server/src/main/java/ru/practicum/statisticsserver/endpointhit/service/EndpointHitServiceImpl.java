package ru.practicum.statisticsserver.endpointhit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.mapping.EndpointHitMapper;
import ru.practicum.statisticsserver.endpointhit.mapping.StatsViewMapper;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;
import ru.practicum.statisticsserver.endpointhit.repository.EndpointHitRepository;
import ru.practicum.statisticsserver.util.StatisticsServerValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    private final StatsViewMapper statsViewMapper;
    private final EndpointHitMapper endpointHitMapper;
    private final StatisticsServerValidator validator;

    @Transactional(readOnly = true)
    @Override
    public List<StatsViewDto> retrieveStatsViewList(LocalDateTime start, LocalDateTime end,
                                                    @Nullable List<String> uris, boolean unique) {
        return getViewListForGivenParams(start, end, uris, unique).equals(Collections.emptyList()) ? Collections.emptyList()
                : getViewListForGivenParams(start, end, uris, unique).stream()
                .map(statsViewMapper::statsViewToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EndpointHitDto saveHit(EndpointHitDto dto) {
        EndpointHit hit = endpointHitMapper.endpointHitFromDto(dto);
        validator.validate(hit);
        return endpointHitMapper.endpointHitToDto(endpointHitRepository.save(hit));
    }

    private List<StatsView> getViewListForGivenParams(LocalDateTime start, LocalDateTime end,
                                                      @Nullable List<String> uris, boolean unique) {
        if (uris == null) {
            if (unique) {
                return endpointHitRepository.getViewListForAllUrisUniqueIp(start, end);
            } else {
                return endpointHitRepository.getViewListForAllUrisNotUniqueIp(start, end);
            }
        } else {
            if (unique) {
                return pasteNullObjectForDefinedUrisIfCountIs0(
                        endpointHitRepository.getViewListForSelectedUrisUniqueIp(start, end, uris), uris);
            } else {
                return pasteNullObjectForDefinedUrisIfCountIs0(
                        endpointHitRepository.getViewListForSelectedUrisNotUniqueIp(start, end, uris), uris);
            }
        }
    }

    /**
     * @param result - лист, возвращаемый репозиторием
     * @param uris   - лист, для значений которого осуществляется запрос в базу
     * @return Возвращает лист результатов, в котором есть результаты из базы (result)
     * и нулевые объекты (для которых нет сущностей EndpointHit - соответственно count=0).
     * Данная реализация позволяет уменьшить степень замешательства при получении результатов,
     * не соответствующих ожидаемым и, например, перепроверить правильность переданного URI.
     */
    private List<StatsView> pasteNullObjectForDefinedUrisIfCountIs0(List<StatsView> result, List<String> uris) {
        List<StatsView> results = new ArrayList<>(result);

        Set<String> resultUris = result.isEmpty() ? Collections.emptySet()
                : result.stream()
                .map(StatsView::getUri)
                .collect(Collectors.toSet());
        uris.forEach(uri -> {
            if (!resultUris.contains(uri)) {
                results.add(new StatsView("stats-server-null-value", uri, 0L));
            }
        });
        return results;
    }
}
