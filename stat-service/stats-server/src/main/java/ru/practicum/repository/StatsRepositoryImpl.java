package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.ViewStatsRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveRequest(EndpointHit endpointHit) {
        String query = "insert into stats(app, uri, ip, created) values (?, ?, ?, ?)";
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
    }

    @Override
    public List<ViewStats> getViewStats(ViewStatsRequest viewStatsRequest) {
        String query = "SELECT app, uri, COUNT (ip) AS hits FROM stats WHERE (created >= ? AND created <= ?) ";
        if (!viewStatsRequest.getUris().isEmpty()) {
            StringBuilder builder = new StringBuilder("AND uri IN ('");
            builder.append(String.join("', '", viewStatsRequest.getUris()));
            builder.append("') ").toString();
            query += builder;
        }
        query += " GROUP BY app, uri ORDER BY hits DESC";
        List<ViewStats> result = jdbcTemplate.query(query, mapRow(), viewStatsRequest.getStart(), viewStatsRequest.getEnd());

        return result;
    }

    @Override
    public List<ViewStats> getUniqueViewStats(ViewStatsRequest viewStatsRequest) {
        String query = "SELECT app, uri, COUNT (DISTINCT ip) AS hits FROM stats WHERE (created >= ? AND created <= ?) ";
        if (!viewStatsRequest.getUris().isEmpty()) {
            String joined = String.join(", ", viewStatsRequest.getUris());
            String uniqueRequest = String.format(" AND uri IN ('%s')", joined);
            query += uniqueRequest;
        }
        query += " GROUP BY app, uri ORDER BY hits DESC";
        List<ViewStats> result = jdbcTemplate.query(query, mapRow(), viewStatsRequest.getStart(), viewStatsRequest.getEnd());

        return result;
    }

    private RowMapper<ViewStats> mapRow() {
        return new RowMapper<ViewStats>() {
            @Override
            public ViewStats mapRow(ResultSet rs, int rowNum) throws SQLException {
                return ViewStats.builder()
                        .app(rs.getString("app"))
                        .uri(rs.getString("uri"))
                        .hits(rs.getInt("hits"))
                        .build();
            }
        };
    }
}
