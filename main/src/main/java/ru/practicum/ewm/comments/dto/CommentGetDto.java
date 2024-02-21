package ru.practicum.ewm.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentGetDto {
    private Integer id;
    private String authorName;
    private String text;
    private LocalDateTime created;
    private Boolean isEdited;
}
