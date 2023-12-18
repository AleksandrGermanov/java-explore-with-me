package ru.practicum.ewmapp.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.comments.dto.CommentFullDto;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.dto.NewCommentDto;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.service.CommentService;
import ru.practicum.ewmapp.comments.service.CommentSortType;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@Validated
@RequiredArgsConstructor
@RestController("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentShortDto> findAllCommentsForUser(@PathVariable Long userId,
                                                       @RequestParam(required = false) Long eventId,
                                                       @RequestParam(required = false) CommentState commentState,
                                                       @RequestParam(defaultValue = "DATE_DESC") CommentSortType sort,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Processing incoming request GET /users/{}/comments. "
                        + "EventId = {}, commentState = commentState = {}, sotType  = {}, from = {}, size = {}.",
                userId, eventId, commentState, sort, from, size);
        return commentService.findAllCommentsForUser(userId, eventId, commentState, sort, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto retrieveComment(@PathVariable Long userId,
                                          @PathVariable Long commentId) {
        log.info("Processing incoming request GET /users/{}/comments/{}.",
                userId, commentId);
        return commentService.retrieveComment(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentShortDto updateComment(@PathVariable Long userId,
                                         @PathVariable Long commentId,
                                         @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Processing incoming request PATCH /users/{}/comments/{}, NewCommentDto = {}.",
                userId, commentId, newCommentDto);
        return commentService.updateComment(userId, commentId, newCommentDto);
    }

    @PatchMapping("/{commentId}/remove")
    public CommentShortDto removeComment(@PathVariable Long userId,
                                         @PathVariable Long commentId) {
        log.info("Processing incoming request PATCH /users/{}/comments/{}/remove.",
                userId, commentId);
        return commentService.removeCommentByUser(userId, commentId);
    }

    @PatchMapping("/{commentId}/restore")
    public CommentShortDto restoreComment(@PathVariable Long userId,
                                          @PathVariable Long commentId) {
        log.info("Processing incoming request PATCH /users/{}/comments/{}/restore.",
                userId, commentId);
        return commentService.restoreComment(userId, commentId);
    }
}
