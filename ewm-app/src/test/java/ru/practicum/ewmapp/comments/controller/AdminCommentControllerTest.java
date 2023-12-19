package ru.practicum.ewmapp.comments.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewmapp.comments.dto.CommentShortDto;
import ru.practicum.ewmapp.comments.service.CommentService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCommentControllerTest {
    @InjectMocks
    private AdminCommentController adminCommentController;
    @Mock
    private CommentService commentService;

    @Test
    void findAllCommentsForAdmin() {
        when(commentService.findAllCommentsForAdmin(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Assertions.assertEquals(Collections.emptyList(), adminCommentController.findAllCommentsForAdmin(
                null, null, null, null, null, null, null));
    }

    @Test
    void moderateComment() {
        when(commentService.moderateComment(any(), any()))
                .thenReturn(new CommentShortDto());

        Assertions.assertEquals(new CommentShortDto(), adminCommentController.moderateComment(
                null, null));
    }

    @Test
    void deleteComment() {
        adminCommentController.deleteComment(0L);

        verify(commentService, times(1)).deleteCommentByAdmin(0L);
    }
}