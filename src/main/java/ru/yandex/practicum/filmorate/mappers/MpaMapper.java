package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;

@Mapper
public interface MpaMapper {
    default MpaDto map(Long id) {
        return new MpaDto(id);
    }

    default Long map(MpaDto mpaDto) {
        return mpaDto.getId();
    }
}
