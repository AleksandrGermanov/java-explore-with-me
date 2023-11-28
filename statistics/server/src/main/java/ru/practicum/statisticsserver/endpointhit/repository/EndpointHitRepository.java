package ru.practicum.statisticsserver.endpointhit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long>, StatsViewRepository {
}
