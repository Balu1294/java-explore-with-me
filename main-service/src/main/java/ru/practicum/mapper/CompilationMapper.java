package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.stream.Collectors;

import static ru.practicum.mapper.EventMapper.toEventShortDto;

@UtilityClass
public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(event -> toEventShortDto(event))
                        .collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
