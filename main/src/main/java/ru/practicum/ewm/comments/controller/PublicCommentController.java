package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comments.CommentService;
import ru.practicum.ewm.comments.dto.CommentGetDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{eventId}")
@Validated
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentGetDto> get(@PathVariable int eventId) {
        return commentService.get(eventId);
    }
}
