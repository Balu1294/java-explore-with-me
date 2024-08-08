package ru.practicum.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class CompilationsController {

    @GetMapping
    public List<Object> getCompilations(boolean pinned, Integer from, Integer size) {
        log.info("");
        return null;
    }

    @GetMapping("/{com-id}")
    public Object getCompilationById(Integer comId) {
        log.info("");
        return null;
    }
}
