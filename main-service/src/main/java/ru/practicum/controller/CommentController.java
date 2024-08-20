package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UpdateCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer commentId) {
        log.info("DELETE запрос на удаление комментария id = {} ", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/admin/comments/search")
    public List<Comment> searchComments(@RequestParam(name = "text") String text,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET Запрос на поиск комментариев c текстом = {}", text);
        return commentService.search(text, from, size);
    }

    @PostMapping("/comments/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Integer userId, @PathVariable Integer eventId, @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST запрос на добавление комментария: {}", newCommentDto);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/users/{userId}/{commentId}")
    public CommentDto patchRequestByUser(@PathVariable Integer userId, @PathVariable Integer commentId, @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("PATCH запрос на обновление пользователем с userId = {}  комментария с commentId = {} ", userId, commentId);
        return commentService.patchByUser(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/comments/users/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer userId, @PathVariable Integer commentId) {
        log.info("DELETE запрос на удаление комментария id = {} пользователем id = {} ", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping("/comments/users/{userId}/comments")
    public List<CommentDto> getRequestListUser(@PathVariable Integer userId) {
        log.info("GET запрос на получение комментариев пользователя с userId = {} ", userId);
        return commentService.getCommentUser(userId);
    }

    @GetMapping("/users/{userId}/{commentId}")
    public Comment getComment(@PathVariable Integer userId, @PathVariable Integer commentId) {
        log.info("GET запрос на получения комментария id = {} пользователем id = {} ", commentId, userId);
        return commentService.getUserCommentByUserAndCommentId(userId, commentId);
    }

    @GetMapping("/comments/events/{eventId}")
    public List<Comment> getRequestListAllCommentsEvent(@PathVariable Integer eventId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId, from, size);
    }
}
