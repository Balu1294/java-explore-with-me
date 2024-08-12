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
        log.info("GET запрос на получения событий с фильтром");
        return eventService.getAllEventFromPublic(searchEventParams, request);
    }

    @GetMapping("/events/{event-id}")
    public EventFullDto getEventById(@PathVariable(value = "event-id") @Min(1) Integer eventId,
                                     HttpServletRequest request) {
        log.info("GET запрос на получения полной информации о событии с  id= {}", eventId);
        return eventService.getEventById(eventId, request);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> searchEvents(@Valid SearchEventParamsAdmin searchEventParamsAdmin) {
        log.info("GET запрос на получение списка событий");
        return eventService.getAllEventFromAdmin(searchEventParamsAdmin);
    }

    @PatchMapping("/admin/events/{event-id}")
    public EventFullDto updateEventByAdmin(@PathVariable(value = "event-id") @Min(1) Integer eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest inputUpdate) {
        log.info("PATCH запрос на обновление списка событий");
        return eventService.updateEventFromAdmin(eventId, inputUpdate);
    }

//    @GetMapping("/users/{user-id}/events")
//    public List<EventShortDto> getAllEventsByUserId(@PathVariable(value = "user-id") @Min(1) Integer userId,
//                                                    @RequestParam(value = "from", defaultValue = "0")
//                                                    @PositiveOrZero Integer from,
//                                                    @RequestParam(value = "size", defaultValue = "10")
//                                                    @Positive Integer size) {
//        log.info("GET запрос на получения событий пользователя с id= {}", userId);
//        return eventService.getEventsByUserId(userId, from, size);
//    }
//
//    @PostMapping("/users/{user-id}/events")
//    @ResponseStatus(HttpStatus.CREATED)
//    public EventFullDto addEvent(@PathVariable(value = "user-id") @Min(1) Integer userId,
//                                 @RequestBody @Valid NewEventDto input) {
//        log.info("POST запрос на создание события от пользователя с id= {}", userId);
//        return eventService.addNewEvent(userId, input);
//    }
//
//    @GetMapping("/users/{user-id}/events/{event-id}")
//    public EventFullDto getFullEventByOwner(@PathVariable(value = "user-id") @Min(1) Integer userId,
//                                            @PathVariable(value = "event-id") @Min(1) Integer eventId) {
//        log.info("GET запрос на получения полной информации о событии для пользователя с id= {}", userId);
//        return eventService.getEventByUserIdAndEventId(userId, eventId);
//    }
//
//    @PatchMapping("/users/{user-id}/events/{event-id}")
//    public EventFullDto updateEventByOwner(@PathVariable(value = "user-id") @Min(0) Integer userId,
//                                           @PathVariable(value = "event-id") @Min(0) Integer eventId,
//                                           @RequestBody @Valid UpdateEventUserRequest inputUpdate) {
//        log.info("PATCH запрос на обновление события от пользователя с id= {}", userId);
//        return eventService.updateEventByUserIdAndEventId(userId, eventId, inputUpdate);
//    }
//
//    @GetMapping("/users/{user-id}/events/{event-id}/requests")
//    public List<ParticipationRequestDto> getAllRequestByEventFromOwner(@PathVariable(value = "user-id") @Min(1) Integer userId,
//                                                                       @PathVariable(value = "event-id") @Min(1) Integer eventId) {
//        log.info("GET запрос на получение информации о всех запросах об участии в событии для пользователя с id= {}", userId);
//        return eventService.getAllParticipationRequestsFromEventByOwner(userId, eventId);
//    }
//
//    @PatchMapping("/users/{user-id}/events/{event-id}/requests")
//    public EventRequestStatusUpdateResult updateStatusRequestFromOwner(@PathVariable(value = "user-id") @Min(1) Integer userId,
//                                                                       @PathVariable(value = "event-id") @Min(1) Integer eventId,
//                                                                       @RequestBody EventRequestStatusUpdateRequest inputUpdate) {
//        log.info("PATCH запрос на обновление статуса события от пользователя с id= {}", userId);
//        return eventService.updateStatusRequest(userId, eventId, inputUpdate);
//    }
}
