package ru.practicum.ewmapp.util;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class CustomRepository<T> {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private final Class<T> typeParameterClass;

    public CustomRepository(EntityManager entityManager, Class<T> typeParameterClass) {
        this.entityManager = entityManager;
        this.typeParameterClass = typeParameterClass;
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public CustomQueryBuilder getBuilder() {
        return new CustomQueryBuilder();
    }

    public interface PredicateWriter<T> {
        List<Predicate> writePredicates(CriteriaBuilder criteriaBuilder, Root<T> root);
    }

    public interface OrderWriter<T> {
        Order writeOrder(CriteriaBuilder criteriaBuilder, Root<T> root);
    }

    public class CustomQueryBuilder {
        private final CriteriaQuery<T> criteriaQuery;
        private final Root<T> root;

        public CustomQueryBuilder() {
            criteriaQuery = criteriaBuilder.createQuery(typeParameterClass);
            root = criteriaQuery.from(typeParameterClass);
        }

        public CustomQueryBuilder setPredicates(PredicateWriter<T> writer) {
            List<Predicate> predicatesList = writer.writePredicates(criteriaBuilder, root);
            return setParams(predicatesList, null);
        }

        public CustomQueryBuilder sortBy(OrderWriter<T> writer) {
            return setParams(null, writer.writeOrder(criteriaBuilder, root));
        }

        public TypedQuery<T> formTypedQuery(int from, int size) {
            int offset = from / size;
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult(offset * size);
            typedQuery.setMaxResults(size);
            return typedQuery;
        }

        private CustomQueryBuilder setParams(List<Predicate> predicates, Order order) {
            if (predicates != null && !predicates.isEmpty()) {
                criteriaQuery.where(predicates.toArray(new Predicate[0]));
            }
            if (order != null) {
                criteriaQuery.orderBy(order);
            }
            return this;
        }
    }
}

