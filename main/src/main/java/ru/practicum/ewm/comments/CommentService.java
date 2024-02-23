package ru.practicum.ewm.comments;

import ru.practicum.ewm.comments.dto.CommentCreateDto;
import ru.practicum.ewm.comments.dto.CommentGetDto;
import ru.practicum.ewm.comments.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentGetDto create(CommentCreateDto dto, int userId);

    CommentGetDto update(CommentUpdateDto dto, int userId);

    void delete(int userId, int comId);

    void deleteByAdmin(int comId);

    List<CommentGetDto> get(int eventId);

    CommentGetDto getByUser(int userId, int comId);

    CommentGetDto getById(int comId);
}
