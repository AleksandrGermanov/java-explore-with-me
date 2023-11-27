package ru.practicum.statisticsserver.endpointhit.repository;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StatsViewRepositoryImpl implements StatsViewRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    @Override
    public List<StatsView> getViewListForAllUrisNotUniqueIp(LocalDateTime start, LocalDateTime end) {
        String allUrisNotUniqueIp = "select eh.app as app, eh.uri as uri, count(eh.id) as hits "
                + "from EndpointHit as eh "
                + "where eh.timestamp >= :start "
                + "and eh.timestamp <= :end "
                + "group by eh.uri, eh.app";
        return entityManager
                .createQuery(allUrisNotUniqueIp, Tuple.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultStream()
                .map(this::mapViewFromTuple)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsView> getViewListForAllUrisUniqueIp(LocalDateTime start, LocalDateTime end) {
        String allUrisUniqueIp = "select eh.app as app, eh.uri as uri, count(distinct eh.ip) as hits "
                + "from EndpointHit as eh "
                + "where eh.timestamp >= :start "
                + "and eh.timestamp <= :end "
                + "group by eh.uri, eh.app";
        return entityManager
                .createQuery(allUrisUniqueIp, Tuple.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultStream()
                .map(this::mapViewFromTuple)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsView> getViewListForSelectedUrisNotUniqueIp(LocalDateTime start, LocalDateTime end,
                                                                 List<String> selectedUris) {
        String selectedUrisNotUniqueIp = "select eh.app as app, eh.uri as uri, count(eh.id) as hits "
                + "from EndpointHit as eh "
                + "where eh.timestamp >= :start "
                + "and eh.timestamp <= :end "
                + "and eh.uri in :uris "
                + "group by eh.uri, eh.app";
        return entityManager
                .createQuery(selectedUrisNotUniqueIp, Tuple.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("uris", selectedUris)
                .getResultStream()
                .map(this::mapViewFromTuple)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsView> getViewListForSelectedUrisUniqueIp(LocalDateTime start, LocalDateTime end,
                                                              List<String> selectedUris) {
        String selectedUrisUniqueIp = "select eh.app as app, eh.uri as uri, count(distinct eh.ip) as hits "
                + "from EndpointHit as eh "
                + "where eh.timestamp >= :start "
                + "and eh.timestamp <= :end "
                + "and eh.uri in :uris "
                + "group by eh.uri, eh.app";
        return entityManager
                .createQuery(selectedUrisUniqueIp, Tuple.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("uris", selectedUris)
                .getResultStream()
                .map(this::mapViewFromTuple)
                .collect(Collectors.toList());
    }

    private StatsView mapViewFromTuple(Tuple tuple) {
        Long hits = tuple.get("hits", Long.class) == null ? 0L : tuple.get("hits", Long.class);
        return new StatsView(tuple.get("app", String.class), tuple.get("uri", String.class), hits);
    }
}
