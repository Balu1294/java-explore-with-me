package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.repository.StatsRepository;

@Service
@Slf4j
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private StatsRepository statsRepository;
    @Override
    public EndpointHit saveHit(EndpointHit endpointHit) {
        return statsRepository.saveRequest(endpointHit);
    }
}
