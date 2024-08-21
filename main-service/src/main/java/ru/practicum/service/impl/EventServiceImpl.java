package ru.practicum.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.dto.CaseUpdatedStatusDto;
import ru.practicum.dto.CountCommentsByEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.AdminStateEvent;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.UserStateEvent;
import ru.practicum.exception.ClashException;
import ru.practicum.exception.IncorrectParametersException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.mapper.EventMapper.*;
import static ru.practicum.mapper.LocationMapper.toLocation;
import static ru.practicum.mapper.RequestMapper.toParticipationRequestDto;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;

    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    @Override
    public List<EventFullDto> getListEventFromAdmin(SearchEventParamsAdmin searchEventParamsAdmin) {
        PageRequest pageable = PageRequest.of(searchEventParamsAdmin.getFrom() / searchEventParamsAdmin.getSize(),
                searchEventParamsAdmin.getSize());
        Specification<Event> specification = Specification.where(null);

        List<Integer> users = searchEventParamsAdmin.getUsers();
        List<String> states = searchEventParamsAdmin.getStates();
        List<Integer> categories = searchEventParamsAdmin.getCategories();
        LocalDateTime rangeEnd = searchEventParamsAdmin.getRangeEnd();
        LocalDateTime rangeStart = searchEventParamsAdmin.getRangeStart();

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("eventStatus").as(String.class).in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        Page<Event> events = eventRepository.findAll(specification, pageable);

        List<EventFullDto> result = events.getContent().stream().map(event ->
                toEventFullDto(event)).collect(Collectors.toList());

        Map<Integer, List<Request>> confirmedRequestsCountMap = getConfirmedRequestsCount(events.toList());
        for (EventFullDto event : result) {
            List<Request> requests = confirmedRequestsCountMap.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequests(requests.size());
        }
        return result;
    }


    @Override
    public EventFullDto updateEventFromAdmin(Integer eventId, UpdateEventAdminRequest updateEvent) {
        Event oldEvent = findEventId(eventId);
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED) || oldEvent.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new ClashException("Можно изменить только неподтвержденное событие");
        }
        boolean hasChanges = false;
        Event eventForUpdate = universalUpdate(oldEvent, updateEvent);
        if (eventForUpdate == null) {
            eventForUpdate = oldEvent;
        } else {
            hasChanges = true;
        }
        LocalDateTime gotEventDate = updateEvent.getEventDate();
        if (gotEventDate != null) {
            if (gotEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new IncorrectParametersException("Некорректные параметры даты.Дата начала "
                        + "изменяемого события должна " + "быть не ранее чем за час от даты публикации.");
            }
            eventForUpdate.setEventDate(updateEvent.getEventDate());
            hasChanges = true;
        }

        AdminStateEvent gotAction = updateEvent.getStateAction();
        if (gotAction != null) {
            if (AdminStateEvent.PUBLISH_EVENT.equals(gotAction)) {
                eventForUpdate.setEventStatus(EventStatus.PUBLISHED);
                hasChanges = true;
            } else if (AdminStateEvent.REJECT_EVENT.equals(gotAction)) {
                eventForUpdate.setEventStatus(EventStatus.CANCELED);
                hasChanges = true;
            }
        }
        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(eventForUpdate);
        }
        return eventAfterUpdate != null ? toEventFullDto(eventAfterUpdate) : null;
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Integer userId, Integer eventId, UpdateEventUserRequest inputUpdate) {
        findUserId(userId);
        Event oldEvent = findEvenByInitiatorAndEventId(userId, eventId);
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ClashException("Статус события не может быть обновлен, так как со статусом PUBLISHED");
        }
        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new ClashException(String.format("Пользователь с id: %d не автор события", userId));
        }
        Event eventForUpdate = universalUpdate(oldEvent, inputUpdate);
        boolean hasChanges = false;
        if (eventForUpdate == null) {
            eventForUpdate = oldEvent;
        } else {
            hasChanges = true;
        }
        LocalDateTime newDate = inputUpdate.getEventDate();
        if (newDate != null) {
            checkDateAndTime(LocalDateTime.now(), newDate);
            eventForUpdate.setEventDate(newDate);
            hasChanges = true;
        }
        UserStateEvent stateAction = inputUpdate.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    eventForUpdate.setEventStatus(EventStatus.PENDING);
                    hasChanges = true;
                    break;
                case CANCEL_REVIEW:
                    eventForUpdate.setEventStatus(EventStatus.CANCELED);
                    hasChanges = true;
                    break;
            }
        }
        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(eventForUpdate);
        }

        return eventAfterUpdate != null ? toEventFullDto(eventAfterUpdate) : null;
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id:%d не найден", userId));
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, org.springframework.data.domain.Sort.by(Sort.Direction.ASC, "id"));
        return eventRepository.findAll(pageRequest).getContent().stream().map(event -> toEventShortDto(event)).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Integer userId, Integer eventId) {
        findUserId(userId);
        Event event = findEvenByInitiatorAndEventId(userId, eventId);
        return toEventFullDto(event);
    }

    @Override
    public EventFullDto addNewEvent(Integer userId, NewEventDto eventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User user = findUserId(userId);
        checkDateAndTime(LocalDateTime.now(), eventDto.getEventDate());
        Category category = findCategoryById(eventDto.getCategory());
        Event event = toEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);
        if (eventDto.getLocation() != null) {
            Location location = locationRepository.save(toLocation(eventDto.getLocation()));
            event.setLocation(location);
        }
        Event eventSaved = eventRepository.save(event);

        EventFullDto eventFullDto = toEventFullDto(eventSaved);
        eventFullDto.setViews(0);
        eventFullDto.setConfirmedRequests(0);
        return eventFullDto;
    }


    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Integer userId, Integer eventId) {
        findUserId(userId);
        findEvenByInitiatorAndEventId(userId, eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(request -> toParticipationRequestDto(request)).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequest(Integer userId, Integer eventId,
                                                              EventRequestStatusUpdateRequest inputUpdate) {
        findUserId(userId);
        Event event = findEvenByInitiatorAndEventId(userId, eventId);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ClashException(String.format("Событие с id: %d не требует подтверждения запросов", eventId));
        }
        RequestStatus status = inputUpdate.getStatus();

        int confirmedRequestsCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit() == confirmedRequestsCount) {
                    throw new ClashException("Лимит участников исчерпан");
                }
                CaseUpdatedStatusDto updatedStatusConfirmed = updatedStatusConfirmed(event, CaseUpdatedStatusDto.builder()
                        .idsFromUpdateStatus(new ArrayList<>(inputUpdate.getRequestIds())).build(), RequestStatus.CONFIRMED, confirmedRequestsCount);

                List<Request> confirmedRequests = requestRepository.findAllById(updatedStatusConfirmed.getProcessedIds());
                List<Request> rejectedRequests = new ArrayList<>();
                if (updatedStatusConfirmed.getIdsFromUpdateStatus().size() != 0) {
                    List<Integer> ids = updatedStatusConfirmed.getIdsFromUpdateStatus();
                    rejectedRequests = rejectRequest(ids, eventId);
                }

                return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmedRequests.stream()
                                .map(request -> toParticipationRequestDto(request)).collect(Collectors.toList()))
                        .rejectedRequests(rejectedRequests.stream().map(request -> toParticipationRequestDto(request))
                                .collect(Collectors.toList())).build();
            case REJECTED:
                if (event.getParticipantLimit() == confirmedRequestsCount) {
                    throw new ClashException("Лимит участников исчерпан");
                }

                final CaseUpdatedStatusDto updatedStatusReject = updatedStatusConfirmed(event,
                        CaseUpdatedStatusDto.builder().idsFromUpdateStatus(new ArrayList<>(inputUpdate.getRequestIds()))
                                .build(), RequestStatus.REJECTED, confirmedRequestsCount);
                List<Request> rejectRequest = requestRepository.findAllById(updatedStatusReject.getProcessedIds());

                return EventRequestStatusUpdateResult.builder().rejectedRequests(rejectRequest.stream()
                        .map(request -> toParticipationRequestDto(request)).collect(Collectors.toList())).build();
            default:
                throw new IncorrectParametersException(String.format("Некорректный статус: %s", status));
        }
    }

    @Override
    public List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request) {

        if (searchEventParams.getRangeEnd() != null && searchEventParams.getRangeStart() != null) {
            if (searchEventParams.getRangeEnd().isBefore(searchEventParams.getRangeStart())) {
                throw new IncorrectParametersException("Дата окончания не может быть раньше даты начала");
            }
        }

        addStatsClient(request);

        Pageable pageable = PageRequest.of(searchEventParams.getFrom() / searchEventParams.getSize(), searchEventParams.getSize());

        Specification<Event> specification = Specification.where(null);
        LocalDateTime now = LocalDateTime.now();

        if (searchEventParams.getText() != null) {
            String searchText = searchEventParams.getText().toLowerCase();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + searchText + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchText + "%")
                    ));
        }

        if (searchEventParams.getCategories() != null && !searchEventParams.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(searchEventParams.getCategories()));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(searchEventParams.getRangeStart(), now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (searchEventParams.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), searchEventParams.getRangeEnd()));
        }

        if (searchEventParams.getOnlyAvailable() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("eventStatus"), EventStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        List<EventShortDto> result = resultEvents
                .stream().map(event -> toEventShortDto(event)).collect(Collectors.toList());
        Map<Integer, Integer> viewStatsMap = getViewsAllEvents(resultEvents);

        List<CountCommentsByEventDto> commentsCountMap = commentRepository.countCommentByEvent(
                resultEvents.stream().map(Event::getId).collect(Collectors.toList()));
        Map<Integer, Long> commentsCountToEventIdMap = commentsCountMap.stream().collect(Collectors.toMap(
                CountCommentsByEventDto::getEventId, CountCommentsByEventDto::getCountComments));

        for (EventShortDto event : result) {
            Integer viewsFromMap = viewStatsMap.getOrDefault(event.getId(), 0);
            event.setViews(viewsFromMap);

            Long commentCountFromMap = commentsCountToEventIdMap.getOrDefault(event.getId(), 0L);
            event.setComments(commentCountFromMap);
        }

        return result;
    }

    @Override
    public EventFullDto getEventById(Integer eventId, HttpServletRequest request) {
        Event event = findEventId(eventId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id: %d не опубликовано", eventId));
        }
        addStatsClient(request);
        EventFullDto eventFullDto = toEventFullDto(event);
        Map<Integer, Integer> viewStatsMap = getViewsAllEvents(List.of(event));
        Integer views = viewStatsMap.getOrDefault(event.getId(), 0);
        eventFullDto.setViews(views);
        return eventFullDto;
    }

    private Event findEventId(Integer eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("События с id: %d не существует", eventId)));
    }

    private User findUserId(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id: %d не существует", userId)));
    }

    private List<Request> findEventAndRequestById(Integer eventId, List<Integer> requestId) {
        return requestRepository.findByEventIdAndIdIn(eventId, requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id: %d или события с id: %d не существуeт", requestId, eventId)));
    }

    private Category findCategoryById(Integer catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категории с id: %d не существует", catId)));
    }

    private Event findEvenByInitiatorAndEventId(Integer userId, Integer eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() ->
                new NotFoundException(String.format("События с id: %d и с пользователем с id: %d не существует", eventId, userId)));
    }

    private void checkDateAndTime(LocalDateTime time, LocalDateTime dateTime) {
        if (dateTime.isBefore(time.plusHours(2))) {
            throw new IncorrectParametersException("Поле должно содержать дату, которая еще не наступила.");
        }
    }

    private Map<Integer, Integer> getViewsAllEvents(List<Event> events) {
        List<String> uris = events.stream().map(event -> String.format("/events/%s", event.getId())).collect(Collectors.toList());

        List<LocalDateTime> startDates = events.stream().map(Event::getCreatedDate).collect(Collectors.toList());
        LocalDateTime earliestDate = startDates.stream().min(LocalDateTime::compareTo).orElse(null);
        Map<Integer, Integer> viewStatsMap = new HashMap<>();

        if (earliestDate != null) {
            ResponseEntity<Object> response = statsClient.getHit(earliestDate, LocalDateTime.now(), uris, true);

            List<ViewStats> viewStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });

            viewStatsMap = viewStatsList.stream()
                    .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(statsDto -> Integer.parseInt(statsDto.getUri()
                            .substring("/events/".length())), ViewStats::getHits));
        }
        return viewStatsMap;
    }

    private CaseUpdatedStatusDto updatedStatusConfirmed(Event event, CaseUpdatedStatusDto caseUpdatedStatus,
                                                        RequestStatus status, int confirmedRequestsCount) {
        int freeRequest = event.getParticipantLimit() - confirmedRequestsCount;
        List<Integer> ids = caseUpdatedStatus.getIdsFromUpdateStatus();
        List<Integer> processedIds = new ArrayList<>();
        List<Request> requestListLoaded = findEventAndRequestById(event.getId(), ids);
        List<Request> requestList = new ArrayList<>();

        for (Request request : requestListLoaded) {
            if (freeRequest == 0) {
                break;
            }

            request.setStatus(status);
            requestList.add(request);

            processedIds.add(request.getId());
            freeRequest--;
        }

        requestRepository.saveAll(requestList);
        caseUpdatedStatus.setProcessedIds(processedIds);
        return caseUpdatedStatus;
    }

    private List<Request> rejectRequest(List<Integer> ids, Integer eventId) {
        List<Request> rejectedRequests = new ArrayList<>();
        List<Request> requestList = new ArrayList<>();
        List<Request> requestListLoaded = findEventAndRequestById(eventId, ids);

        for (Request request : requestListLoaded) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                break;
            }
            request.setStatus(RequestStatus.REJECTED);
            requestList.add(request);
            rejectedRequests.add(request);
        }
        requestRepository.saveAll(requestList);
        return rejectedRequests;
    }

    private void addStatsClient(HttpServletRequest request) {
        statsClient.saveHit(EndpointHit.builder().app(applicationName)
                .uri(request.getRequestURI()).ip(request.getRemoteAddr()).timestamp(LocalDateTime.now()).build());
    }

    private Map<Integer, List<Request>> getConfirmedRequestsCount(List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events.stream().map(Event::getId)
                .collect(Collectors.toList()), RequestStatus.CONFIRMED);
        return requests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId()));
    }

    private Event universalUpdate(Event oldEvent, UpdateEventRequest updateEvent) {
        boolean hasChanges = false;
        String gotAnnotation = updateEvent.getAnnotation();
        if (gotAnnotation != null && !gotAnnotation.isBlank()) {
            oldEvent.setAnnotation(gotAnnotation);
            hasChanges = true;
        }
        Integer gotCategory = updateEvent.getCategory();
        if (gotCategory != null) {
            Category category = findCategoryById(gotCategory);
            oldEvent.setCategory(category);
            hasChanges = true;
        }
        String gotDescription = updateEvent.getDescription();
        if (gotDescription != null && !gotDescription.isBlank()) {
            oldEvent.setDescription(gotDescription);
            hasChanges = true;
        }
        if (updateEvent.getLocation() != null) {
            Location location = toLocation(updateEvent.getLocation());
            oldEvent.setLocation(location);
            hasChanges = true;
        }
        Integer gotParticipantLimit = updateEvent.getParticipantLimit();
        if (gotParticipantLimit != null) {
            oldEvent.setParticipantLimit(gotParticipantLimit);
            hasChanges = true;
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
            hasChanges = true;
        }
        Boolean requestModeration = updateEvent.getRequestModeration();
        if (requestModeration != null) {
            oldEvent.setRequestModeration(requestModeration);
            hasChanges = true;
        }
        String gotTitle = updateEvent.getTitle();
        if (gotTitle != null && !gotTitle.isBlank()) {
            oldEvent.setTitle(gotTitle);
            hasChanges = true;
        }
        if (!hasChanges) {

            oldEvent = null;
        }
        return oldEvent;
    }
}
