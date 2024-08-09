package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.mappers.FeedMapper;
import ru.yandex.practicum.filmorate.mappers.FeedMapperImpl;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper = new FeedMapperImpl();
    private final FeedStorage feedStorage;

    public List<FeedDto> getFeeds(Long id) {
        return feedStorage.getFeed(id).stream().map(feedMapper::toDto).toList();
    }
}
