package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.mappers.FeedMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final FeedMapper feedMapper;
    private UserService userService;

    @Autowired
    void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    public List<FeedDto> getFeeds(Long userId) {
        User user = userService.getUserById(userId);
        return feedStorage.getFeed(user.getId()).stream().map(feedMapper::toDto).toList();
    }

    public void create(Long userId, EventType eventType, Operation operation, Long entityId) {
        User user = userService.getUserById(userId);
        Feed feed = new Feed(
                null,
                Instant.now().toEpochMilli(),
                user.getId(),
                eventType,
                operation,
                entityId);
        feedStorage.save(feed);
    }
}