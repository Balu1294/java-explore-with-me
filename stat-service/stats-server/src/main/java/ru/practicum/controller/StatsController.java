package ru.practicum.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHit;
import ru.practicum.service.StatsService;

@RestController
@RequestMapping
@Slf4j
@AllArgsConstructor
public class StatsController {
    StatsService service;

    @PostMapping("/hit")
    public EndpointHit saveHit(@RequestBody EndpointHit endpointHit) {
        log.info("Поступил запрос на создание EndpointHit");
        return service.saveHit(endpointHit);
    }

}
