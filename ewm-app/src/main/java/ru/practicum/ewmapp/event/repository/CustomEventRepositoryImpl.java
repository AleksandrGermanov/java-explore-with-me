package ru.practicum.ewmapp.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.event.model.EventState;

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
@RequiredArgsConstructor
public class CustomEventRepositoryImpl implements CustomEventRepository{
    private final EntityManager entityManager;
     public List<Event> findAllEventsForAdmin(List<Long> userIds, List<EventState> states,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Integer from, Integer size){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if(userIds != null && !userIds.isEmpty()){
            predicates.add(eventRoot.get("initiator").get("id").as(Long.class).in(userIds));
        }
        if(states != null && !states.isEmpty()){
            predicates.add(eventRoot.get("state").as(EventState.class).in(states));
        }
        if(rangeStart != null){
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    Timestamp.valueOf(rangeStart)));
        }
        if(rangeEnd != null){
            predicates.add(criteriaBuilder.lessThanOrEqualTo(eventRoot.get("eventDate").as(Timestamp.class),
                    Timestamp.valueOf(rangeEnd)));
        }

        if(!predicates.isEmpty()){
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
        int offset = from/size;
        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(offset*size);
        typedQuery.setMaxResults(size);
        return typedQuery.getResultList();
    }

}
