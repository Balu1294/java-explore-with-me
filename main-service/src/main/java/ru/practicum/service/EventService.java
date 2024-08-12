package ru.practicum.service;

import ru.practicum.dto.*;
import ru.practicum.dto.event.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventFromAdmin(SearchEventParamsAdmin searchEventParamsAdmin);

    EventFullDto updateEventFromAdmin(Integer eventId, UpdateEventAdminRequest inputUpdate);

    List<EventShortDto> getEventsByUserId(Integer userId, Integer from, Integer size);

    EventFullDto addNewEvent(Integer userId, NewEventDto input);

    EventFullDto getEventByUserIdAndEventId(Integer userId, Integer eventId);

    EventFullDto updateEventByUserIdAndEventId(Integer userId, Integer eventId, UpdateEventUserRequest inputUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateStatusRequest(Integer userId, Integer eventId, EventRequestStatusUpdateRequest inputUpdate);

    List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request);

    EventFullDto getEventById(Integer eventId, HttpServletRequest request);
}
