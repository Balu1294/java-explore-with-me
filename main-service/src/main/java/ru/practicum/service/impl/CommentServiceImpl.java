package ru.practicum.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.enums.EventStatus;
import ru.practicum.exception.IncorrectParametersException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CommentMapper.toComment;
import static ru.practicum.mapper.CommentMapper.toCommentDto;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CommentDto updateComment(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto) {
        User user = getUserById(userId);
        Comment comment = getCommentById(commentId);
        checkAuthorComment(user, comment);
        LocalDateTime updateTime = LocalDateTime.now();

        if (updateTime.isAfter(comment.getCreated().plusHours(1L))) {
            throw new IncorrectParametersException("Время на изменения комментария 1 час");
        }

        comment.setText(updateCommentDto.getText());
        comment.setLastUpdatedOn(updateTime);
        return toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUser(Integer userId) {
        getUserById(userId);
        List<Comment> commentList = commentRepository.findByAuthor_Id(userId);
        return commentList.stream().map(comment -> toCommentDto(comment)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getUserCommentByUserAndCommentId(Integer userId, Integer commentId) {
        getUserById(userId);
        return commentRepository.findByAuthor_IdAndId(userId, commentId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id: %d не писал комментарий с id: %d", userId, commentId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByEvent(Integer eventId, Integer from, Integer size) {
        getEventById(eventId);
        PageRequest pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEvent_Id(eventId, pageable);

    }

    @Override
    public void removeComment(Integer userId, Integer commentId) {
        User user = getUserById(userId);
        Comment comment = getCommentById(commentId);
        checkAuthorComment(user, comment);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public CommentDto addComment(Integer userId, Integer eventId, NewCommentDto commentDto) {
        Event event = getEventById(eventId);
        User user = getUserById(userId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new IncorrectParametersException("Комментарий можно добавить только к событию со статусом PUBLISHED");
        }
        return toCommentDto(commentRepository.save(toComment(commentDto, event, user)));
    }

    private User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя c id: %d  не существует", id)));
    }

    private Event getEventById(Integer id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("События с id: %d  не существует", id)));
    }

    private Comment getCommentById(Integer id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Комментария c id: %d  не существует", id)));
    }

    private void checkAuthorComment(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            throw new IncorrectParametersException(String.format("Пользователь с id: %d не автор комментария c id: %d",
                    user.getId(), comment.getId()));
        }
    }
}