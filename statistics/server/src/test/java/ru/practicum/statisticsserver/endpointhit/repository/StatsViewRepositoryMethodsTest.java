package ru.practicum.statisticsserver.endpointhit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatsViewRepositoryMethodsTest {
    private final EndpointHitRepository endpointHitRepository;
    LocalDateTime start;
    LocalDateTime end;
    List<String> uris;
    EndpointHit hit;
    StatsView view;

    @BeforeEach
    public void setup() {
        start = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        end = LocalDateTime.of(2023, 2, 2, 2, 2, 2);
        uris = List.of("/uri");
        hit = new EndpointHit(null, "app", "/uri", "0.0.0.0", start);
        view = new StatsView("app", "/uri", 1L);
        endpointHitRepository.deleteAll();
        endpointHitRepository.save(hit);
    }

    @Test
    public void getViewListForAllUrisNotUniqueIpTest() {
        Assertions.assertEquals(List.of(view), endpointHitRepository.getViewListForAllUrisNotUniqueIp(start, end));

        EndpointHit hit2 = new EndpointHit(null, "app", "/uri", "0.0.0.0", end);
        view.setCount(2L);
        endpointHitRepository.save(hit2);
        Assertions.assertEquals(List.of(view), endpointHitRepository.getViewListForAllUrisNotUniqueIp(start, end));

        Assertions.assertEquals(Collections.emptyList(),
                endpointHitRepository.getViewListForAllUrisNotUniqueIp(start.minusHours(1), start.minusSeconds(1)));
        Assertions.assertEquals(Collections.emptyList(),
                endpointHitRepository.getViewListForAllUrisNotUniqueIp(end.plusSeconds(1), end.plusHours(1)));
    }

    @Test
    public void getViewListForAllUrisUniqueIpTest() {
        Assertions.assertEquals(List.of(view), endpointHitRepository.getViewListForAllUrisUniqueIp(start, end));

        EndpointHit hit2 = new EndpointHit(null, "app", "/uri", "0.0.0.1", end);
        view.setCount(2L);
        endpointHitRepository.save(hit2);
        Assertions.assertEquals(List.of(view), endpointHitRepository.getViewListForAllUrisUniqueIp(start, end));

        EndpointHit hit3 = new EndpointHit(null, "app", "/uri", "0.0.0.0", start);
        endpointHitRepository.save(hit3);
        Assertions.assertEquals(List.of(view), endpointHitRepository.getViewListForAllUrisUniqueIp(start, end));

        Assertions.assertEquals(Collections.emptyList(),
                endpointHitRepository.getViewListForAllUrisUniqueIp(start.minusHours(1), start.minusSeconds(1)));
        Assertions.assertEquals(Collections.emptyList(),
                endpointHitRepository.getViewListForAllUrisUniqueIp(end.plusSeconds(1), end.plusHours(1)));
    }

    @Test
    public void getViewListForSelectedUrisNotUniqueIpTest() {
        Assertions.assertEquals(List.of(view),
                endpointHitRepository.getViewListForSelectedUrisNotUniqueIp(start, end, uris));

        EndpointHit hit2 = new EndpointHit(null, "app", "/uri", "0.0.0.0", end);
        view.setCount(2L);
        endpointHitRepository.save(hit2);
        Assertions.assertEquals(List.of(view),
                endpointHitRepository.getViewListForSelectedUrisNotUniqueIp(start, end, uris));

        Assertions.assertEquals(Collections.emptyList(), endpointHitRepository
                .getViewListForSelectedUrisNotUniqueIp(start.minusHours(1), start.minusSeconds(1), uris));
        Assertions.assertEquals(Collections.emptyList(), endpointHitRepository
                .getViewListForSelectedUrisNotUniqueIp(end.plusSeconds(1), end.plusHours(1), uris));
        Assertions.assertEquals(Collections.emptyList(), endpointHitRepository
                .getViewListForSelectedUrisNotUniqueIp(start, end, List.of("")));
    }

    @Test
    public void getViewListForSelectedUrisUniqueIpTest() {
        Assertions.assertEquals(List.of(view),
                endpointHitRepository.getViewListForSelectedUrisUniqueIp(start, end, uris));

        EndpointHit hit2 = new EndpointHit(null, "app", "/uri", "0.0.0.1", end);
        view.setCount(2L);
        endpointHitRepository.save(hit2);
        Assertions.assertEquals(List.of(view),
                endpointHitRepository.getViewListForSelectedUrisUniqueIp(start, end, uris));

        EndpointHit hit3 = new EndpointHit(null, "app", "/uri", "0.0.0.1", end);
        endpointHitRepository.save(hit3);
        Assertions.assertEquals(List.of(view),
                endpointHitRepository.getViewListForSelectedUrisUniqueIp(start, end, uris));

        Assertions.assertEquals(Collections.emptyList(), endpointHitRepository
                .getViewListForSelectedUrisUniqueIp(start.minusHours(1), start.minusSeconds(1), uris));
        Assertions.assertEquals(Collections.emptyList(), endpointHitRepository
                .getViewListForSelectedUrisUniqueIp(end.plusSeconds(1), end.plusHours(1), uris));
        Assertions.assertEquals(Collections.emptyList(), endpointHitRepository
                .getViewListForSelectedUrisUniqueIp(start, end, List.of("")));
    }
}
