package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    List<Feed> getFeed(Long userId);

    void save(FeedDto feedDto);
}
