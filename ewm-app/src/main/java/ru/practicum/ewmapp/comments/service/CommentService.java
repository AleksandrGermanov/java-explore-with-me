package ru.practicum.ewmapp.comments.service;

import ru.practicum.ewmapp.comments.dto.CommentFullDto;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.dto.NewCommentDto;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;

import java.util.List;

public interface CommentService {
    CommentShortDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    List<CommentShortDto> findAllCommentsForEvent(Long userId, Long eventId, UserState userState,
                                                  CommentState commentState, Integer from, Integer size);

    List<CommentShortDto> findAllCommentsForUser(Long userId, Long eventId, CommentState commentState,
                                                 CommentSortType sortType, Integer from, Integer size);

    CommentFullDto retrieveComment(Long userId, Long commentId);

    CommentShortDto updateComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    CommentShortDto removeCommentByUser(Long userId, Long commentId);

    CommentShortDto restoreComment(Long userId, Long commentId);

    List<CommentShortDto> findAllCommentsForAdmin(Long eventId, List<Long> userIds, UserState userState,
                                                  CommentState commentState, CommentSortType sort,
                                                  Integer from, Integer size);

    CommentShortDto moderateComment(Long commentId, NewCommentDto newCommentDto);

    void deleteCommentByAdmin(Long commentId);
}
