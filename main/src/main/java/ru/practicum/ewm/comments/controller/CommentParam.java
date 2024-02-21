package ru.practicum.ewm.comments.controller;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.comments.dto.CommentCreateDto;
import ru.practicum.ewm.comments.dto.CommentUpdateDto;

@Getter
@Setter
public class CommentParam {
    private int userId;
    private int eventId;
    private int comId;
    private CommentCreateDto createDto;
    private CommentUpdateDto dto;
}
