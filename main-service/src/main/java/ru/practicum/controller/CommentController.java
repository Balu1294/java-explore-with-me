package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class CommentController {
    private final ReportService reportService;
    private final CommentService commentService;

    @PostMapping("/admin/comments/reports/{reportId}")
    public Report updateReportStatus(@PathVariable Long reportId) {
        return null;
    }

    @GetMapping("/admin/comments/reports")
    public List<Long> getListReportsByStatus(@RequestBody ReportStatus status) {
        log.info("GET запрос на получение жалоб со статусом " + status);
        return reportService.getListReportsByStatus(status);
    }

    @PostMapping("/comments/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("POST запрос на добавление комментария: {}", newCommentDto);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/users/{userId}/{commentId}")
    public CommentDto patchRequestByUser(@PathVariable Long userId, @PathVariable Long commentId,
                                         @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("PATCH запрос на обновление пользователем с userId = {}  комментария с commentId = {} ", userId, commentId);
        return commentService.patchByUser(userId, commentId, updateCommentDto);
    }

    @GetMapping("/comments/users/{userId}/comments")
    public List<CommentDto> getRequestListUser(@PathVariable Long userId) {
        log.info("GET запрос на получение комментариев пользователя с userId = {} ", userId);
        return commentService.getCommentUser(userId);
    }


    @GetMapping("/comments/events /{eventId}")
    public List<CommentDto> getRequestListAllCommentsEvent(@PathVariable Long eventId) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return commentService.getCommentEvent(eventId);
    }

    @PostMapping("/comments/{commentId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void addReport(@PathVariable Long commentId) {
        log.info("Опубликована жалоба на комментарий с id = {} ", commentId);
        reportService.addReport(commentId);
    }

}
