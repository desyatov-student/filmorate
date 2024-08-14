package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface FeedMapper {
    FeedDto toDto(Feed feed);
}
