package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.service.CommentService;

import java.util.List;

import static ru.practicum.constant.PathConstant.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments/users/{user-id}/events/{event-id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable(USER_ID) Integer userId,
                                 @PathVariable(EVENT_ID) Integer eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Поступил  запрос на добавление комментария: {}", newCommentDto.getText());
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping(COMMENTS_USER_ID_AND_COMMENT_ID_PATH)
    public CommentDto updateComment(@PathVariable(USER_ID) Integer userId,
                                    @PathVariable(COMMENT_ID) Integer commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("Поступил запрос на обновление пользователем с userId = {}  комментария с commentId = {} ", userId, commentId);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @GetMapping("/comments/users/{user-id}/comments")
    public List<CommentDto> getCommentsByUser(@PathVariable(USER_ID) Integer userId) {
        log.info("Поступил запрос на получение комментариев пользователя с userId = {} ", userId);
        return commentService.getCommentsByUser(userId);
    }

    @DeleteMapping(COMMENTS_USER_ID_AND_COMMENT_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable(USER_ID) Integer userId,
                              @PathVariable(COMMENT_ID) Integer commentId) {
        log.info("Поступил запрос на удаление комментария id = {} пользователем id = {} ", userId, commentId);
        commentService.removeComment(userId, commentId);
    }

    @GetMapping(COMMENTS_USER_ID_AND_COMMENT_ID_PATH)
    public Comment getCommentById(@PathVariable(USER_ID) Integer userId,
                                  @PathVariable(COMMENT_ID) Integer commentId) {
        log.info("Поступил запрос на получения комментария id = {} пользователем id = {} ", commentId, userId);
        return commentService.getUserCommentByUserAndCommentId(userId, commentId);
    }


    @GetMapping("/comments/{event-id}")
    public List<Comment> getCommentsByEvent(@PathVariable(EVENT_ID) Integer eventId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentsByEvent(eventId, from, size);
    }
}
