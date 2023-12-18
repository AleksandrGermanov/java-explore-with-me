package ru.practicum.ewmapp.event.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.service.PublicEventSortType;
import ru.practicum.ewmapp.util.CustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomEventRepositoryImpl extends CustomRepository<Event> implements CustomEventRepository {

    public CustomEventRepositoryImpl(@Autowired EntityManager entityManager) {
        super(entityManager, Event.class);
    }

    @Override
    public List<Event> findAllEventsForUser(String text, List<Long> categoryIds, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Boolean onlyAvailable, PublicEventSortType sort,
                                            Integer from, Integer size) {
        return getBuilder()
                .setPredicates((criteriaBuilder, root)->predicatesForUser(criteriaBuilder, root,
                        text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable))
                .sortBy((criteriaBuilder, root) -> sort == null ? null : sort.getOrder(criteriaBuilder, root))
                .formTypedQuery(from, size)
                .getResultList();
    }

    public List<Event> findAllEventsForAdmin(List<Long> userIds, List<EventState> states,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from, Integer size) {
        return getBuilder()
                .setPredicates(((criteriaBuilder, root) -> predicatesForAdmin(criteriaBuilder, root,
                        userIds, states, rangeStart, rangeEnd)))
                .formTypedQuery(from, size)
                .getResultList();
    }

    private List<Predicate> predicatesForUser(CriteriaBuilder criteriaBuilder, Root<Event> eventRoot,
                                              String text, List<Long> categoryIds, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Boolean onlyAvailable){
        List<Predicate> predicates = new ArrayList<>();

        if (text != null && !text.isEmpty()) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.upper(eventRoot.get("annotation").as(String.class)),
                            '%' + text.toUpperCase() + '%'
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.upper(eventRoot.get("description").as(String.class)),
                            '%' + text.toUpperCase() + '%'
                    )
            ));
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            predicates.add(eventRoot.get("category").get("id").as(Long.class).in(categoryIds));
        }
        if (paid != null) {
            predicates.add(criteriaBuilder.equal(eventRoot.get("paid").as(Boolean.class), paid));
        }
        if (rangeStart != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    Timestamp.valueOf(rangeStart)));
        }
        if (rangeEnd != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    Timestamp.valueOf(rangeEnd)));
        }
        if (rangeStart == null && rangeEnd == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    criteriaBuilder.currentTimestamp()));
        }
        if (onlyAvailable) {
            predicates.add(criteriaBuilder.greaterThan(eventRoot.get("participantLimit").as(Integer.class),
                    criteriaBuilder.size(eventRoot.get("requestsForEvent"))));
        }
        return predicates;
    }

    private List<Predicate> predicatesForAdmin (CriteriaBuilder criteriaBuilder, Root<Event> eventRoot,
                                                List<Long> userIds, List<EventState> states,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd){
        List<Predicate> predicates = new ArrayList<>();

        if (userIds != null && !userIds.isEmpty()) {
            predicates.add(eventRoot.get("initiator").get("id").as(Long.class).in(userIds));
        }
        if (states != null && !states.isEmpty()) {
            predicates.add(eventRoot.get("state").as(EventState.class).in(states));
        }
        if (rangeStart != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    Timestamp.valueOf(rangeStart)));
        }
        if (rangeEnd != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    Timestamp.valueOf(rangeEnd)));
        }
        return predicates;
    }
}
