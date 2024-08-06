package ru.practicum.repository;

import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.ViewStatsRequest;

import java.util.List;

public interface StatsRepository {
    void saveRequest(EndpointHit endpointHit);

    List<ViewStats> getViewStats(ViewStatsRequest viewStatsRequest);

    List<ViewStats> getUniqueViewStats(ViewStatsRequest request);
}
