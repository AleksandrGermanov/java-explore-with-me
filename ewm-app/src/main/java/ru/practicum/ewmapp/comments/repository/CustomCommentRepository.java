package ru.practicum.ewmapp.comments.repository;

import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.service.CommentSortType;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> findAllCommentsForEvent(Long eventId, UserState userState,
                                          CommentState commentState, Integer from, Integer size);

    List<Comment> findAllCommentsForUser(Long userId, Long eventId, CommentState commentState,
                                         CommentSortType sortType, Integer from, Integer size);

    List<Comment> findAllCommentsForAdmin(Long eventId, List<Long> userIds,
                                          UserState userState, CommentState commentState,
                                          CommentSortType sort, Integer from, Integer size);
}
