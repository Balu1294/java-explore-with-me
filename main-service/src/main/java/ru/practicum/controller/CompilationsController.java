package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.mapper.CompilationMapper.toCompilationDto;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationsController {

    private final CompilationService service;

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение списка compilation");
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{com-id}")
    public CompilationDto getCompilationById(@PathVariable("com-id") Integer comId) {
        log.info("Поступил запрос на получение compilation c id: {}", comId);
        Compilation compilation = service.findByIdCompilation(comId);
        return toCompilationDto(compilation);
    }

    @PostMapping("/admin/compilations")
    public CompilationDto addNewCompilations(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Поступил запрос на создание сompilation с заголовком: {}", newCompilationDto.getTitle());
        return service.addCompilation(newCompilationDto);
    }

    @PatchMapping("/admin/compilations/{com-id}")
    public CompilationDto updateCompilation(@RequestBody @Valid UpdateCompilationRequest updateCompilationRequest,
                                            @PathVariable("com-id") Integer compId) {
        log.info("Поступил запрос на обновление compilation c id: {}", compId);
        return service.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/admin/compilations/{com-id}")
    public void removeCompilation(@PathVariable("com-id") Integer comId) {
        log.info("Поступил запрос на удаление compilation c id: {}", comId);
        service.deleteCompilation(comId);
    }
}
