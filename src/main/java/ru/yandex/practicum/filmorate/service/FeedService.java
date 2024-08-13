package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.mappers.FeedMapper;
import ru.yandex.practicum.filmorate.mappers.FeedMapperImpl;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final FeedMapper feedMapper = new FeedMapperImpl();

    public List<FeedDto> getFeeds(Long id) {
        return feedStorage.getFeed(id).stream().map(feedMapper::toDto).toList();
    }

    public Feed create(Long userId, EventType eventType, Operation operation, Long entityId) {
        return new Feed(
                null,
                Instant.now().toEpochMilli(),
                userId,
                eventType,
                operation,
                entityId);
    }
}
