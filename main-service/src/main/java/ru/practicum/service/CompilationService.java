package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {


    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest update);

    void removeCompilation(Integer compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Integer compId);
}
