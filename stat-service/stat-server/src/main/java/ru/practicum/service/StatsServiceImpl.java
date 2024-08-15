package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.ViewStatsRequest;
import ru.practicum.repository.StatsRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveHit(EndpointHit endpointHit) {
        statsRepository.saveRequest(endpointHit);
    }

    @Override
    public List<ViewStats> getViewStats(ViewStatsRequest request) {
        if (request.getUris() == null) {
            request.setUris(Collections.emptyList());
        }
        if (request.isUnique()) {
            return statsRepository.getUniqueViewStats(request);
        }
        return statsRepository.getViewStats(request);

    }
}
