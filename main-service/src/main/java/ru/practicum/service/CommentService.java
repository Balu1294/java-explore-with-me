package ru.practicum.service;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Integer userId, Integer eventId, NewCommentDto commentDto);

    CommentDto patchByUser(Integer userId, Integer commentId, UpdateCommentDto updateCommentDto);

    List<CommentDto> getCommentUser(Integer userId);

    List<CommentDto> getCommentEvent(Integer eventId);

    void deleteComment(Integer userId, Integer commentId);
}
