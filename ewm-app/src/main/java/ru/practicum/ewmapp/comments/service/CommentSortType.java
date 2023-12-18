package ru.practicum.ewmapp.comments.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewmapp.comments.model.Comment;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;


@RequiredArgsConstructor
public enum CommentSortType {
    DATE_ASC{
        @Override
        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<Comment> root) {
            return criteriaBuilder.asc(root.get("createdOn"));
        }
    },
    DATE_DESC {
        @Override
        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<Comment> root) {
            return criteriaBuilder.desc(root.get("createdOn"));
        }
    },
    COMMENTATOR_ID {
        @Override
        public Order getOrder(CriteriaBuilder criteriaBuilder, Root<Comment> root) {
            return criteriaBuilder.desc(root.get("commentator").get("id"));
        }
    };

    public abstract Order getOrder(CriteriaBuilder criteriaBuilder, Root<Comment> root);
}
