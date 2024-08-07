package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

@Mapper
public interface FeedMapper {
    FeedDto toDto(Feed feed);
}
