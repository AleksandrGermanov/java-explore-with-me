package ru.practicum.ewmapp.event.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;
import ru.practicum.ewmapp.event.service.PublicEventSortType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomEventRepositoryImpl implements CustomEventRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CustomEventRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public List<Event> findAllEventsForUser(String text, List<Long> categoryIds, Boolean paid,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Boolean onlyAvailable, PublicEventSortType sort,
                                            Integer from, Integer size) {
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);
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
                    criteriaBuilder.size(eventRoot.get("requestsForEvent")))); //три часа потратил
            //на эту строчку - коллекции считаются методом size, а не count... И никто об этом не пишет!
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        if (sort != null && sort.equals(PublicEventSortType.EVENT_DATE)) {
            criteriaQuery.orderBy(criteriaBuilder.desc(eventRoot.get("eventDate")));
        }

        int offset = from / size;
        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(offset * size);
        typedQuery.setMaxResults(size);
        return typedQuery.getResultList();
    }

    public List<Event> findAllEventsForAdmin(List<Long> userIds, List<EventState> states,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from, Integer size) {
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);
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

        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
        int offset = from / size;
        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(offset * size);
        typedQuery.setMaxResults(size);
        return typedQuery.getResultList();
    }
}
