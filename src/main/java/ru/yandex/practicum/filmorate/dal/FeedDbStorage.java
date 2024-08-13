package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class FeedDbStorage extends BaseDbStorage<Feed> implements FeedStorage {
    private static final String FIND_FEED_BY_ID_QUERY = "SELECT * FROM feeds where user_id = ?";
    private static final String PUT_EVENT_QUERY = """
            INSERT INTO feeds (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Feed> getFeed(Long userId) {
        return findMany(FIND_FEED_BY_ID_QUERY, userId);
    }

    @Override
    public Feed save(Feed feed) {
        Long eventId = insert(
                PUT_EVENT_QUERY,
                new Timestamp(feed.getTimestamp()),
                feed.getUserId(),
                feed.getEventType().toString(),
                feed.getOperation().toString(),
                feed.getEntityId()
        );
        feed.setEventId(eventId);
        return feed;
    }
}
