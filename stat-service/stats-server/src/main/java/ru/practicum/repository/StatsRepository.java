package ru.practicum.repository;

import ru.practicum.EndpointHit;

public interface StatsRepository {
    EndpointHit saveRequest(EndpointHit endpointHit);
}
