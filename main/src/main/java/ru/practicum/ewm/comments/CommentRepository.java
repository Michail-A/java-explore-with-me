package ru.practicum.ewm.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByEventIdOrderByCreatedDesc(int eventId);
}
