package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.controller.CommentParam;
import ru.practicum.ewm.comments.dto.CommentGetDto;
import ru.practicum.ewm.comments.dto.CommentMapper;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentGetDto create(CommentParam param) {
        User user = userRepository.findById(param.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + param.getUserId() + " не найден"));
        Event event = eventRepository.findById(param.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id=" + param.getEventId() + " не найден"));

        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Событие id=" + param.getEventId() + " еще не опубликовано");
        }
        Comment comment = CommentMapper.toModel(param.getCreateDto(), user, event);

        return CommentMapper.toCommentGetDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentGetDto update(CommentParam param) {
        userRepository.findById(param.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + param.getUserId() + " не найден"));
        Comment comment = commentRepository.findById(param.getComId())
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + param.getComId() + " не найден"));

        if (comment.getEvent().getId() != param.getEventId()) {
            throw new ValidationException("Комментарий id= " + param.getComId() + " не относится к событию id="
                    + param.getEventId());
        }
        if (comment.getAuthor().getId() != param.getUserId()) {
            throw new ValidationException("Комментарий id=  " + param.getComId() + " не принадлежит пользователю id=" + param.getUserId());
        }

        comment.setText(param.getDto().getText());
        comment.setIsEdited(true);

        return CommentMapper.toCommentGetDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(CommentParam param) {
        userRepository.findById(param.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + param.getUserId() + " не найден"));
        eventRepository.findById(param.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id=" + param.getEventId() + " не найден"));
        Comment comment = commentRepository.findById(param.getComId())
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + param.getComId() + " не найден"));

        if (comment.getAuthor().getId() != param.getUserId()) {
            throw new AlreadyAvailableException("Комментарий id=" + param.getComId() + " принадлежит другому пользователю");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteByAdmin(int comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + comId + " не найден"));
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentGetDto> get(int eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найден"));
        List<Comment> comments = commentRepository.findByEventIdOrderByCreatedDesc(eventId);

        return comments.stream()
                .map(CommentMapper::toCommentGetDto)
                .collect(Collectors.toList());
    }
}
