package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feed.FeedBuilder builder = Feed.builder();

        builder.timestamp(rs.getLong("timestamp"));
        builder.userId(rs.getLong("user_id"));
        builder.eventType(EventType.valueOf(rs.getString("event_type")));
        builder.operation(Operation.valueOf(rs.getString("operation")));
        builder.eventId(rs.getLong("event_id"));
        builder.entityId(rs.getLong("entity_id"));
        return builder.build();
    }
}
