package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentCreateDto;
import ru.practicum.ewm.comments.dto.CommentGetDto;
import ru.practicum.ewm.comments.dto.CommentMapper;
import ru.practicum.ewm.comments.dto.CommentUpdateDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.Status;
import ru.practicum.ewm.exception.AlreadyAvailableException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.UserRepository;

import java.time.LocalDateTime;
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
    public CommentGetDto create(CommentCreateDto dto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id=" + dto.getEventId() + " не найден"));

        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new AlreadyAvailableException("Событие id=" + dto.getEventId() + " еще не опубликовано");
        }
        Comment comment = CommentMapper.toModel(dto, user, event);

        return CommentMapper.toCommentGetDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentGetDto update(CommentUpdateDto dto, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Comment comment = commentRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + dto.getId() + " не найден"));


        comment.setText(dto.getText());
        comment.setIsEdited(true);
        comment.setEdited(LocalDateTime.now());

        return CommentMapper.toCommentGetDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(int userId, int comId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Comment comment = commentRepository.findByIdAndAuthorId(comId, userId);

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

    @Override
    public CommentGetDto getByUser(int userId, int comId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Comment comment = commentRepository.findByIdAndAuthorId(comId, userId);
        return CommentMapper.toCommentGetDto(comment);
    }

    @Override
    public CommentGetDto getById(int comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + comId + " не найден"));
        return CommentMapper.toCommentGetDto(comment);
    }
}
