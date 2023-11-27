package ru.practicum.statisticsserver.endpointhit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.commondtolib.EndpointHitDto;
import ru.practicum.commondtolib.StatsViewDto;
import ru.practicum.statisticsserver.endpointhit.mapping.EndpointHitMapper;
import ru.practicum.statisticsserver.endpointhit.mapping.StatsViewMapper;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;
import ru.practicum.statisticsserver.endpointhit.repository.EndpointHitRepository;
import ru.practicum.statisticsserver.util.StatisticsServerValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EndpointHitServiceImplTest {
    @Mock
    private EndpointHitRepository endpointHitRepository;
    @Mock
    private StatsViewMapper statsViewMapper;
    @Mock
    private EndpointHitMapper endpointHitMapper;
    @Mock
    private StatisticsServerValidator statisticsServerValidator;
    @InjectMocks
    private EndpointHitServiceImpl service;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;

    private StatsView view;
    private StatsViewDto viewDto;
    private List<StatsViewDto> viewDtoList;
    private List<StatsView> viewList;


    @BeforeEach
    public void setup() {
        start = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        end = LocalDateTime.of(2023, 2, 2, 2, 2, 2);
        uris = List.of("/uri");
        view = new StatsView("app", "/uri", 1L);
        viewDto = new StatsViewDto("app", "/uri", 1L);
        viewDtoList = List.of(new StatsViewDto("app", "/uri", 1L));
        viewList = List.of(new StatsView("app", "/uri", 1L));
    }

    @Test
    public void methodSaveHitCallsMapperValidatorAndRepository() {
        final EndpointHitDto dto = new EndpointHitDto(null, "app", "/uri", "0.0.0.0", start);
        final EndpointHit hit = new EndpointHit(null, "app", "/uri", "0.0.0.0", start);

        when(endpointHitMapper.endpointHitFromDto(dto)).thenReturn(hit);
        when(endpointHitRepository.save(hit)).thenReturn(hit);
        when(endpointHitMapper.endpointHitToDto(hit)).thenReturn(dto);

        Assertions.assertEquals(dto, service.saveHit(dto));
        verify(statisticsServerValidator, times(1)).validate(hit);
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndNullFalseReturnsViewDtoList() {
        when(endpointHitRepository.getViewListForAllUrisNotUniqueIp(start, end)).thenReturn(viewList);
        when(statsViewMapper.statsViewToDto(view)).thenReturn(viewDto);

        Assertions.assertEquals(viewDtoList, service.retrieveStatsViewList(start, end, null, false));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndNullFalseWhenRepositoryReturnsNullReturnsEmptyList() {
        when(endpointHitRepository.getViewListForAllUrisNotUniqueIp(start, end)).thenReturn(null);

        Assertions.assertEquals(Collections.emptyList(),
                service.retrieveStatsViewList(start, end, null, false));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndNullTrueReturnsViewDtoList() {
        when(endpointHitRepository.getViewListForAllUrisUniqueIp(start, end)).thenReturn(viewList);
        when(statsViewMapper.statsViewToDto(view)).thenReturn(viewDto);

        Assertions.assertEquals(viewDtoList, service.retrieveStatsViewList(start, end, null, true));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndNullTrueWhenRepositoryReturnsNullReturnsEmptyList() {
        when(endpointHitRepository.getViewListForAllUrisUniqueIp(start, end)).thenReturn(null);

        Assertions.assertEquals(Collections.emptyList(),
                service.retrieveStatsViewList(start, end, null, true));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndUrisFalseReturnsViewDtoList() {
        when(endpointHitRepository.getViewListForSelectedUrisNotUniqueIp(start, end, uris)).thenReturn(viewList);
        when(statsViewMapper.statsViewToDto(view)).thenReturn(viewDto);

        Assertions.assertEquals(viewDtoList, service.retrieveStatsViewList(start, end, uris, false));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndUrisFalseWhenRepositoryReturnsNullReturnsViewDtoList() {
        StatsView nullValue = new StatsView("stats-server-null-value", "/uri", 0L);
        StatsViewDto nullValueDto = new StatsViewDto("stats-server-null-value", "/uri", 0L);

        when(endpointHitRepository.getViewListForSelectedUrisNotUniqueIp(start, end, uris)).thenReturn(null);
        when(statsViewMapper.statsViewToDto(nullValue)).thenReturn(nullValueDto);

        Assertions.assertEquals(List.of(nullValueDto), service.retrieveStatsViewList(start, end, uris, false));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndUrisTrueReturnsViewDtoList() {
        when(endpointHitRepository.getViewListForSelectedUrisUniqueIp(start, end, uris)).thenReturn(viewList);
        when(statsViewMapper.statsViewToDto(view)).thenReturn(viewDto);

        Assertions.assertEquals(viewDtoList, service.retrieveStatsViewList(start, end, uris, true));
    }

    @Test
    public void methodRetrieveStatsViewWithParamsStartEndUrisTrueWhenRepositoryReturnsNullReturnsViewDtoList() {
        StatsView nullValue = new StatsView("stats-server-null-value", "/uri", 0L);
        StatsViewDto nullValueDto = new StatsViewDto("stats-server-null-value", "/uri", 0L);

        when(endpointHitRepository.getViewListForSelectedUrisUniqueIp(start, end, uris)).thenReturn(null);
        when(statsViewMapper.statsViewToDto(nullValue)).thenReturn(nullValueDto);

        Assertions.assertEquals(List.of(nullValueDto), service.retrieveStatsViewList(start, end, uris, true));
    }
}
