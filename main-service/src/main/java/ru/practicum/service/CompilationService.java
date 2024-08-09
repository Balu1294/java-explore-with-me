package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationService {


    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation findByIdCompilation(Integer compId);

    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest update);

    void deleteCompilation(Integer compId);
}
