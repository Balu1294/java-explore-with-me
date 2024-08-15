package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> getAllEvents(@Valid SearchEventParams searchEventParams,
                                            HttpServletRequest request) {
        log.info("Поступил запрос на получение событий с фильтром");
        return eventService.getAllEventFromPublic(searchEventParams, request);
    }

    @GetMapping("/events/{event-id}")
    public EventFullDto getEventById(@PathVariable("event-id") @Min(1) Integer eventId,
                                     HttpServletRequest request) {
        log.info("Поступил запрос на получение полной информации о событии с  id= {}", eventId);
        return eventService.getEventById(eventId, request);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getListEvents(@Valid SearchEventParamsAdmin searchEventParamsAdmin) {
        log.info("Поступил запрос на получение списка событий");
        return eventService.getListEventFromAdmin(searchEventParamsAdmin);
    }

    @PatchMapping("/admin/events/{event-id}")
    public EventFullDto updateEventByAdmin(@PathVariable("event-id") @Min(1) Integer eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest eventUpdate) {
        log.info("Поступил запрос на обновление списка событий");
        return eventService.updateEventFromAdmin(eventId, eventUpdate);
    }

    @GetMapping("/users/{user-id}/events")
    public List<EventShortDto> getAllEventsByUserId(@PathVariable("user-id") @Min(1) Integer userId,
                                                    @RequestParam(value = "from", defaultValue = "0")
                                                    @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10")
                                                    @Positive Integer size) {
        log.info("Поступил запрос на получения событий пользователя с id= {}", userId);
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/users/{user-id}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable("user-id") @Min(1) Integer userId,
                                 @RequestBody @Valid NewEventDto input) {
        log.info("Поступил запрос на создание события от пользователя с id= {}", userId);
        return eventService.addNewEvent(userId, input);
    }

    @GetMapping("/users/{user-id}/events/{event-id}")
    public EventFullDto getFullEventByUser(@PathVariable("user-id") @Min(1) Integer userId,
                                           @PathVariable("event-id") @Min(1) Integer eventId) {
        log.info("Поступил запрос на получения полной информации о событии для пользователя с id= {}", userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/users/{user-id}/events/{event-id}")
    public EventFullDto updateEventByOwner(@PathVariable("user-id") @Min(0) Integer userId,
                                           @PathVariable("event-id") @Min(0) Integer eventId,
                                           @RequestBody @Valid UpdateEventUserRequest inputUpdate) {
        log.info("Поступил запрос на обновление события от пользователя с id= {}", userId);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, inputUpdate);
    }

    @GetMapping("/users/{user-id}/events/{event-id}/requests")
    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable("user-id") @Min(1) Integer userId,
                                                                       @PathVariable("event-id") @Min(1) Integer eventId) {
        log.info("Поступил запрос на получение информации о всех запросах об участии в событии для пользователя с id= {}", userId);
        return eventService.getAllParticipationRequestsFromEventByOwner(userId, eventId);
    }

    @PatchMapping("/users/{user-id}/events/{event-id}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestFromOwner(@PathVariable("user-id") @Min(1) Integer userId,
                                                                       @PathVariable("event-id") @Min(1) Integer eventId,
                                                                       @RequestBody EventRequestStatusUpdateRequest inputUpdate) {
        log.info("Поступил запрос на обновление статуса события от пользователя с id= {}", userId);
        return eventService.updateStatusRequest(userId, eventId, inputUpdate);
    }
}
