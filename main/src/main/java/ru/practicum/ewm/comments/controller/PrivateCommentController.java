package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.CommentService;
import ru.practicum.ewm.comments.dto.CommentCreateDto;
import ru.practicum.ewm.comments.dto.CommentGetDto;
import ru.practicum.ewm.comments.dto.CommentUpdateDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments/{eventId}")
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentGetDto create(@RequestBody @Valid CommentCreateDto dto,
                                @PathVariable int userId,
                                @PathVariable int eventId) {
        CommentParam param = new CommentParam();
        param.setCreateDto(dto);
        param.setUserId(userId);
        param.setEventId(eventId);

        return commentService.create(param);
    }

    @PatchMapping("/comment/{comId}")
    public CommentGetDto update(@RequestBody @Valid CommentUpdateDto dto,
                                @PathVariable int userId,
                                @PathVariable int eventId,
                                @PathVariable int comId) {
        CommentParam param = new CommentParam();
        param.setDto(dto);
        param.setUserId(userId);
        param.setEventId(eventId);
        param.setComId(comId);

        return commentService.update(param);
    }

    @DeleteMapping("/comment/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int userId,
                       @PathVariable int eventId,
                       @PathVariable int comId) {
        CommentParam param = new CommentParam();
        param.setUserId(userId);
        param.setEventId(eventId);
        param.setComId(comId);

        commentService.delete(param);
    }
}
