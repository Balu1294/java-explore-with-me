package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.exception.IncorrectParametersException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CompilationMapper.toCompilation;
import static ru.practicum.mapper.CompilationMapper.toCompilationDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = toCompilation(compilationDto);
        compilation.setPinned(Optional.ofNullable(compilation.getPinned()).orElse(false));

        Set<Integer> compEventIds = (compilationDto.getEvents() != null) ? compilationDto.getEvents() : Collections.emptySet();
        List<Integer> eventIds = new ArrayList<>(compEventIds);
        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        Set<Event> eventsSet = new HashSet<>(events);
        compilation.setEvents(eventsSet);

        Compilation compilationAfterSave = compilationRepository.save(compilation);
        return toCompilationDto(compilationAfterSave);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest update) {
        Compilation compilation = findById(compId);

        Set<Integer> eventIds = update.getEvents();

        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(new ArrayList<>(eventIds));
            Set<Event> eventSet = new HashSet<>(events);
            compilation.setEvents(eventSet);
        }

        compilation.setPinned(Optional.ofNullable(update.getPinned()).orElse(compilation.getPinned()));
        if (compilation.getTitle().isBlank()) {
            throw new IncorrectParametersException("Title не может состоять из пробелов");
        }
        compilation.setTitle(Optional.ofNullable(update.getTitle()).orElse(compilation.getTitle()));

        return toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public void removeCompilation(Integer compId) {
        findById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }

        return compilations.stream()
                .map(compilation -> toCompilationDto(compilation))
                .collect(Collectors.toList());
    }


    @Override
    public CompilationDto getCompilationById(Integer compId) {
        return toCompilationDto(findById(compId));
    }

    private Compilation findById(Integer compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException(String.format("Compilation с id: %d не существует", compId)));
    }
}
