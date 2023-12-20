package ru.practicum.ewmapp.comments.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.service.CommentSortType;
import ru.practicum.ewmapp.util.CustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomCommentRepositoryImpl extends CustomRepository<Comment> implements CustomCommentRepository {

    public CustomCommentRepositoryImpl(@Autowired EntityManager entityManager) {
        super(entityManager, Comment.class);
    }

    @Override
    public List<Comment> findAllCommentsForEvent(Long eventId, UserState userState,
                                                 CommentState commentState, Integer from, Integer size) {
        return getBuilder()
                .setPredicates((criteriaBuilder, commentRoot) -> predicatesForEvent(criteriaBuilder, commentRoot,
                        eventId, userState, commentState))
                .formTypedQuery(from, size)
                .getResultList();
    }

    @Override
    public List<Comment> findAllCommentsForUser(Long userId, Long eventId, CommentState commentState,
                                                CommentSortType sort, Integer from, Integer size) {
        return getBuilder()
                .setPredicates((criteriaBuilder, commentRoot) -> predicatesForUser(criteriaBuilder,
                        commentRoot, userId, eventId, commentState))
                .sortBy(sort::getOrder)
                .formTypedQuery(from, size)
                .getResultList();
    }

    @Override
    public List<Comment> findAllCommentsForAdmin(Long eventId, List<Long> userIds,
                                                 UserState userState, CommentState commentState,
                                                 CommentSortType sort, Integer from, Integer size) {
        return getBuilder()
                .setPredicates((criteriaBuilder, commentRoot) -> predicatesForAdmin(criteriaBuilder, commentRoot,
                        eventId, userIds, userState, commentState))
                .sortBy(sort::getOrder)
                .formTypedQuery(from, size)
                .getResultList();
    }

    private List<Predicate> predicatesForEvent(CriteriaBuilder criteriaBuilder, Root<Comment> commentRoot,
                                               Long eventId, UserState userState,
                                               CommentState commentState) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(commentRoot.get("event").get("id").as(Long.class), eventId));
        if (userState != null) {
            predicates.add(criteriaBuilder.equal(
                    commentRoot.get("userState").as(UserState.class), userState));
        }
        if (commentState != null) {
            predicates.add(criteriaBuilder.equal(
                    commentRoot.get("commentState").as(CommentState.class), commentState));
        }
        return predicates;
    }


    private List<Predicate> predicatesForUser(CriteriaBuilder criteriaBuilder, Root<Comment> commentRoot,
                                              Long userId, Long eventId, CommentState commentState) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(commentRoot.get("commentator").get("id").as(Long.class), userId));
        if (eventId != null) {
            predicates.add(criteriaBuilder.equal(commentRoot.get("event").get("id").as(Long.class), eventId));
        }
        if (commentState != null) {
            predicates.add(criteriaBuilder.equal(
                    commentRoot.get("commentState").as(CommentState.class), commentState));
        }
        return predicates;
    }

    private List<Predicate> predicatesForAdmin(CriteriaBuilder criteriaBuilder, Root<Comment> commentRoot,
                                               Long eventId, List<Long> userIds,
                                               UserState userState, CommentState commentState) {
        List<Predicate> predicates = new ArrayList<>();

        if (eventId != null) {
            predicates.add(criteriaBuilder.equal(commentRoot.get("event").get("id").as(Long.class), eventId));
        }
        if (userIds != null && !userIds.isEmpty()) {
            predicates.add(commentRoot.get("commentator").get("id").as(Long.class).in(userIds));
        }
        if (userState != null) {
            predicates.add(criteriaBuilder.equal(
                    commentRoot.get("userState").as(UserState.class), userState));
        }
        if (commentState != null) {
            predicates.add(criteriaBuilder.equal(
                    commentRoot.get("commentState").as(CommentState.class), commentState));
        }
        return predicates;
    }
}
