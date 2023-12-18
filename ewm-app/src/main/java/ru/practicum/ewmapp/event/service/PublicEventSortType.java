package ru.practicum.ewmapp.event.service;

import ru.practicum.ewmapp.event.model.Event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public enum PublicEventSortType {
    EVENT_DATE {
        @Override
        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<Event> root) {
            return criteriaBuilder.desc(root.get("eventDate"));
        }
    },
    VIEWS {
        @Override
        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<Event> root) {
            return null;
        }
    },
    MOST_DISCUSSED {
        @Override
        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<Event> root) {
            return criteriaBuilder.desc(criteriaBuilder.size(root.get("comments")));
        }
    };

    public abstract Order getOrder(CriteriaBuilder criteriaBuilder, Root<Event> root);
    }
