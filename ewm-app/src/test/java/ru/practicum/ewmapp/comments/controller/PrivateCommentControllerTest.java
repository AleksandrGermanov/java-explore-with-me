package ru.practicum.ewmapp.comments.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmapp.comments.dto.CommentFullDto;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.service.CommentService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateCommentControllerTest {
    @InjectMocks
    private PrivateCommentController privateCommentController;
    @Mock
    private CommentService commentService;

    @Test
    void findAllCommentsForUser() {
        when(commentService.findAllCommentsForUser(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Assertions.assertEquals(Collections.emptyList(), privateCommentController.findAllCommentsForUser(
                0L, null, null, null, null, null));
    }

    @Test
    void retrieveComment() {
        when(commentService.retrieveComment(any(), any()))
                .thenReturn(new CommentFullDto());

        Assertions.assertEquals(new CommentFullDto(),
                privateCommentController.retrieveComment(null, null));
    }

    @Test
    void updateComment() {
        when(commentService.updateComment(any(), any(), any()))
                .thenReturn(new CommentShortDto());

        Assertions.assertEquals(new CommentShortDto(),
                privateCommentController.updateComment(null, null, null));
    }

    @Test
    void removeComment() {
        when(commentService.removeCommentByUser(any(), any()))
                .thenReturn(new CommentShortDto());

        Assertions.assertEquals(new CommentShortDto(),
                privateCommentController.removeComment(null, null));
    }

    @Test
    void restoreComment() {
        when(commentService.restoreComment(any(), any()))
                .thenReturn(new CommentShortDto());

        Assertions.assertEquals(new CommentShortDto(),
                privateCommentController.restoreComment(null, null));
    }
}