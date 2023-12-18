package ru.practicum.ewmapp.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmapp.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
}
