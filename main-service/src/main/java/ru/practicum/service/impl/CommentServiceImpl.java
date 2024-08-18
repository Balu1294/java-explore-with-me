package ru.practicum.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.enums.EventStatus;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CommentDto patchByUser(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthorComment(user, comment);
        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentUser(Integer userId) {
        checkUser(userId);
        List<Comment> commentList = commentRepository.findByAuthor_Id(userId);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentEvent(Integer eventId) {
        Event event = checkEvent(eventId);
        List<Comment> commentList = commentRepository.findAllByEventId(eventId);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Integer userId, Integer commentId) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthorComment(user, comment);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    @Override
    public CommentDto createComment(Integer userId, Integer eventId, NewCommentDto commentDto) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException("Невозможно добавить комментарий к событию со статусом не PUBLISHED");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, event, user)));
    }

    private Event checkEvent(Integer id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Событие с id=%d  не найдено", id)));
    }

    private User checkUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d  не найден", id)));
    }

    private Comment checkComment(Integer id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d  не найден", id)));
    }

    private void checkAuthorComment(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            throw new NotFoundException("Пользователь не является автором комментария");
        }
    }
}
