package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;

import java.util.List;

import static ru.practicum.constant.PathConstant.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping("/events")
    public List<EventShortDto> getAllEvents(@Valid SearchEventParams searchEventParams,
                                            HttpServletRequest request) {
        log.info("Поступил запрос на получение событий с фильтром");
        return eventService.getAllEventFromPublic(searchEventParams, request);
    }

    @GetMapping("/events/{event-id}")
    public EventFullDto getEventById(@PathVariable(EVENT_ID) @Min(1) Integer eventId,
                                     HttpServletRequest request) {
        log.info("Поступил запрос на получение полной информации о событии с  id= {}", eventId);
        return eventService.getEventById(eventId, request);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getListEvents(@Valid SearchEventParamsAdmin searchEventParamsAdmin) {
        log.info("Поступил запрос на получение списка событий");
        return eventService.getListEventFromAdmin(searchEventParamsAdmin);
    }

    @PatchMapping(ADMIN_EVENTS_PATH)
    public EventFullDto updateEventByAdmin(@PathVariable(EVENT_ID) @Min(1) Integer eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest eventUpdate) {
        log.info("Поступил запрос на обновление списка событий");
        return eventService.updateEventFromAdmin(eventId, eventUpdate);
    }

    @GetMapping(USERS_EVENTS_PATH)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable(USER_ID) @Min(1) Integer userId,
                                                    @RequestParam(value = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10")
                                                    @Positive Integer size) {
        log.info("Поступил запрос на получения событий пользователя с id= {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping(USERS_EVENTS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable(USER_ID) @Min(1) Integer userId,
                                 @RequestBody @Valid NewEventDto input) {
        log.info("Поступил запрос на создание события от пользователя с id= {}", userId);
        return eventService.addNewEvent(userId, input);
    }

    @GetMapping(USERS_ID_EVENTS_ID_PATH)
    public EventFullDto getFullEventByUser(@PathVariable(USER_ID) @Min(1) Integer userId,
                                           @PathVariable(EVENT_ID) @Min(1) Integer eventId) {
        log.info("Поступил запрос на получения полной информации о событии для пользователя с id= {}", userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping(USERS_ID_EVENTS_ID_PATH)
    public EventFullDto updateEventByOwner(@PathVariable(USER_ID) @Min(0) Integer userId,
                                           @PathVariable(EVENT_ID) @Min(0) Integer eventId,
                                           @RequestBody @Valid UpdateEventUserRequest inputUpdate) {
        log.info("Поступил запрос на обновление события от пользователя с id= {}", userId);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, inputUpdate);
    }

    @GetMapping(USERS_ID_EVENTS_ID_REQUESTS_PATH)
    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable(USER_ID) @Min(1) Integer userId,
                                                                       @PathVariable(EVENT_ID) @Min(1) Integer eventId) {
        log.info("Поступил запрос на получение информации о всех запросах об участии в событии для пользователя с id= {}", userId);
        return eventService.getAllParticipationRequestsFromEventByOwner(userId, eventId);
    }

    @PatchMapping(USERS_ID_EVENTS_ID_REQUESTS_PATH)
    public EventRequestStatusUpdateResult updateStatusRequestFromOwner(@PathVariable(USER_ID) @Min(1) Integer userId,
                                                                       @PathVariable(EVENT_ID) @Min(1) Integer eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest inputUpdate) {
        log.info("Поступил запрос на обновление статуса события от пользователя с id= {}", userId);
        return eventService.updateStatusRequest(userId, eventId, inputUpdate);
    }
}
