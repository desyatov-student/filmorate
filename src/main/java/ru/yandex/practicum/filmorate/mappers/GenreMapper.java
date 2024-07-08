package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@Mapper
public interface GenreMapper {
    GenreDto map(Genre genre);
}
