package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments/{comId}")
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int comId) {
        commentService.deleteByAdmin(comId);
    }
}
