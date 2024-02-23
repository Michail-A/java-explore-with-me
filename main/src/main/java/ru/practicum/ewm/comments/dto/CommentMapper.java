package ru.practicum.ewm.comments.dto;

import ru.practicum.ewm.comments.Comment;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.users.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toModel(CommentCreateDto dto, User user, Event event) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setIsEdited(false);
        comment.setEvent(event);
        return comment;
    }

    public static CommentGetDto toCommentGetDto(Comment comment) {
        CommentGetDto dto = new CommentGetDto();
        dto.setId(comment.getId());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setIsEdited(comment.getIsEdited());
        if (dto.getIsEdited()) {
            dto.setEdited(comment.getEdited());
        }
        return dto;
    }
}
