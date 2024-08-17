package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import jakarta.validation.constraints.Min;
import java.util.List;

import static ru.practicum.constant.PathConstant.*;

@RestController
@RequestMapping(USERS_REQUESTS_PATH)
@Slf4j
@RequiredArgsConstructor
@Validated
public class RequestsController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable(USER_ID) @Min(0) Integer userId,
                                              @RequestParam(EVENT_ID) @Min(0) Integer eventId) {
        log.info("Поступил запрос на создание запроса на участие в событии с id= {}  пользователя с id= {}",
                eventId, userId);
        return requestService.addNewRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(USER_ID) @Min(0) Integer userId) {
        log.info("Поступил запрос на получение всех запросов на участие в событиях пользователя с id= {}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{request-id}/cancel")
    public ParticipationRequestDto canceledRequest(@PathVariable(USER_ID) @Min(0) Integer userId,
                                                   @PathVariable(REQUEST_ID) @Min(0) Integer requestId) {
        log.info("Поступил запрос на отмену запроса пользователем с id= {}", userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
