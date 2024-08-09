package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feed.FeedBuilder builder = Feed.builder();

        builder.id(rs.getLong("id"));
        builder.timestamp(rs.getLong("timestamp"));
        builder.userId(rs.getLong("user_id"));
        builder.eventType(rs.getString("event_type"));
        builder.eventId(rs.getLong("event_id"));
        builder.entityId(rs.getLong("entity_id"));
        return builder.build();
    }
}
