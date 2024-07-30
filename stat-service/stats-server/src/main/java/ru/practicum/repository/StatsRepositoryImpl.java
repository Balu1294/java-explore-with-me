package ru.practicum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.EndpointHit;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Objects;

@Component
public class StatsRepositoryImpl implements StatsRepository {
    JdbcTemplate jdbcTemplate;

    @Override
    public EndpointHit saveRequest(EndpointHit endpointHit) {
        String query = "insert into endpoint_hit(app, uri, ip, timestamp) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"id"});
            stmt.setString(1, endpointHit.getApp());
            stmt.setString(2, endpointHit.getUri());
            stmt.setString(3, endpointHit.getIp());
            stmt.setTimestamp(4, Timestamp.valueOf(endpointHit.getTimestamp()));
            return stmt;
        }, keyHolder);
        endpointHit.setId(Objects.requireNonNull(keyHolder.getKey().intValue()));
        return endpointHit;
    }

}
