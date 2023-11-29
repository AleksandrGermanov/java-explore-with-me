package ru.practicum.statisticsserver.endpointhit.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statisticsserver.endpointhit.model.StatsView;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StatsViewRepositoryImpl implements StatsViewRepository {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    @Override
    public List<StatsView> getViewListForAllUrisNotUniqueIp(LocalDateTime start, LocalDateTime end) {
        String allUrisNotUniqueIp = QueryStringTemplate.stringFor(Unique.FALSE, Uris.ALL);
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
        String allUrisUniqueIp = QueryStringTemplate.stringFor(Unique.TRUE, Uris.ALL);
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
        String selectedUrisNotUniqueIp = QueryStringTemplate.stringFor(Unique.FALSE, Uris.SELECTED);
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
        String selectedUrisUniqueIp = QueryStringTemplate.stringFor(Unique.TRUE, Uris.SELECTED);
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

    private enum Unique {
        TRUE("distinct eh.ip"),
        FALSE("eh.id");
        final String fragment;

        Unique(String fragment) {
            this.fragment = fragment;
        }
    }

    private enum Uris {
        ALL(""),
        SELECTED("and eh.uri in :uris ");
        final String fragment;

        Uris(String fragment) {
            this.fragment = fragment;
        }
    }

    private static class QueryStringTemplate {
        static final String INIT_VALUE = "select eh.app as app, eh.uri as uri, count(%s) as hits "
                + "from EndpointHit as eh "
                + "where eh.timestamp >= :start "
                + "and eh.timestamp <= :end "
                + "%s"
                + "group by eh.uri, eh.app "
                + "order by hits desc";

        private static String stringFor(Unique unique, Uris uris) {
            return String.format(INIT_VALUE, unique.fragment, uris.fragment);
        }
    }
}
