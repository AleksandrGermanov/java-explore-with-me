package ru.practicum.ewmapp.comments.repository;

import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.model.Comment;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.service.CommentSortType;
import ru.practicum.ewmapp.event.model.Event;
import ru.practicum.ewmapp.user.model.User;

import java.util.List;

public interface CustomCommentRepository {

    List<Comment> findAllCommentsForEvent(Event event, UserState userState,
                                          CommentState commentState, Integer from, Integer size);

    List<Comment> findAllCommentsForUser(User user, Event event, CommentState commentState,
                                         CommentSortType sortType, Integer from, Integer size);

    List<Comment> findAllCommentsForAdmin(Event event, List<Long> userIds,
                                                  UserState userState, CommentState commentState,
                                                  CommentSortType sort, Integer from, Integer size);
}
