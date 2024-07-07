package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;

@Mapper
public interface GenreMapper {
    default GenreDto map(Long id) {
        return new GenreDto(id);
    }

    default Long map(GenreDto genreDto) {
        return genreDto.getId();
    }
}
