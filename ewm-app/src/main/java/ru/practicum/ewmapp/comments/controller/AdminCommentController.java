package ru.practicum.ewmapp.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.dto.NewCommentDto;
import ru.practicum.ewmapp.comments.model.CommentState;
import ru.practicum.ewmapp.comments.model.UserState;
import ru.practicum.ewmapp.comments.service.CommentService;
import ru.practicum.ewmapp.comments.service.CommentSortType;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentShortDto> findAllCommentsForAdmin(@RequestParam(required = false) Long eventId,
                                                         @RequestParam(required = false) List<Long> userIds,
                                                         @RequestParam(required = false) UserState userState,
                                                         @RequestParam(required = false) CommentState commentState,
                                                         @RequestParam(defaultValue = "DATE_DESC") CommentSortType sort,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Processing incoming request GET /admin/comments. "
                        + "EventId = {}, userIds = {}, userState = {}, commentState = commentState = {}," +
                        " sotType  = {}, from = {}, size = {}.",
                eventId, userIds, userState, commentState, sort, from, size);
        return commentService.findAllCommentsForAdmin(eventId, userIds, userState, commentState, sort, from, size);
    }

    @PatchMapping({"/{commentId}"})
    public CommentShortDto moderateComment(@PathVariable Long commentId,
                                           @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Processing incoming request PATCH /admin/comments/{}, NewCommentDto = {}.",
                commentId, newCommentDto);
        return commentService.moderateComment(commentId, newCommentDto);
    }

    @DeleteMapping({"/{commentId}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("Processing incoming request PATCH /admin/comments/{}, NewCommentDto = {}.",
                commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}