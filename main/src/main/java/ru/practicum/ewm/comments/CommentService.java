package ru.practicum.ewm.comments;

import ru.practicum.ewm.comments.controller.CommentParam;
import ru.practicum.ewm.comments.dto.CommentGetDto;

import java.util.List;

public interface CommentService {
    CommentGetDto create(CommentParam param);

    CommentGetDto update(CommentParam param);

    void delete(CommentParam param);

    void deleteByAdmin(int comId);

    List<CommentGetDto> get(int eventId);
}
