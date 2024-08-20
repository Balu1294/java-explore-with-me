package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Integer userId, Integer eventId, NewCommentDto commentDto);

    CommentDto patchByUser(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentUser(Integer userId);

    Comment getUserCommentByUserAndCommentId(Integer userId, Integer commentId);

    List<Comment> getCommentEvent(Integer eventId, Integer from, Integer size);

    void deleteComment(Integer userId, Integer commentId);

    void deleteCommentByAdmin(Integer commentId);

    List<Comment> search(String text, Integer from, Integer size);
}
