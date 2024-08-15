package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ClashException;
import ru.practicum.exception.IncorrectParametersException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.RequestMapper.toParticipationRequestDto;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto addNewRequest(Integer userId, Integer eventId) {
        User user = findByIdUser(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
        LocalDateTime createdOn = LocalDateTime.now();
        requestValidate(event, userId, eventId);
        Request request = new Request();
        request.setCreated(createdOn);
        request.setRequester(user);
        request.setEvent(event);

        if (event.isRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        requestRepository.save(request);

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Integer userId) {
        findByIdUser(userId);
        List<Request> result = requestRepository.findAllByRequesterId(userId);
        return result.stream().map(request -> toParticipationRequestDto(request)).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        findByIdUser(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с id: %d не найден", requestId)));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new IncorrectParametersException("Запрос не подтвержден");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request requestAfterSave = requestRepository.save(request);
        return toParticipationRequestDto(requestAfterSave);
    }

    private User findByIdUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя с id: %d не существует", userId)));
    }

    private void requestValidate(Event event, Integer userId, Integer eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ClashException(String.format("Пользователь с id: %d не инициатор события", userId));
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <=
                requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ClashException("Превышен лимит участников события");
        }
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ClashException(String.format("Событие: %s не опубликовано", event.getTitle()));
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ClashException(String.format("События с id: %d уже существует", eventId));
        }
    }
}
