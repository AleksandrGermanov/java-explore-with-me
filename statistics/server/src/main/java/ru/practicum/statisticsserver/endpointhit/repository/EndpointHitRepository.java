package ru.practicum.statisticsserver.endpointhit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.statisticsserver.endpointhit.model.EndpointHit;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long>, StatsViewRepository {
}
