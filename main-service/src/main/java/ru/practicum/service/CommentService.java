package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Integer userId, Integer eventId, NewCommentDto commentDto);

    CommentDto updateComment(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentsByUser(Integer userId);

    Comment getUserCommentByUserAndCommentId(Integer userId, Integer commentId);

    List<Comment> getCommentsByEvent(Integer eventId, Integer from, Integer size);

    void removeComment(Integer userId, Integer commentId);
}
