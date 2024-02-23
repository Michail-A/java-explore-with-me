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
@RequestMapping("/users/{userId}/comments")
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentGetDto create(@RequestBody @Valid CommentCreateDto dto,
                                @PathVariable int userId) {

        return commentService.create(dto, userId);
    }

    @PatchMapping
    public CommentGetDto update(@RequestBody @Valid CommentUpdateDto dto,
                                @PathVariable int userId) {


        return commentService.update(dto, userId);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int userId,
                       @PathVariable int comId) {
        commentService.delete(userId, comId);
    }

    @GetMapping("/{comId}")
    public CommentGetDto getByUser(@PathVariable int userId,
                                   @PathVariable int comId) {
        return commentService.getByUser(userId, comId);
    }
}
