package ru.yandex.practicum.filmorate.dto.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<GenreDto> genres;
    private MpaDto mpa;
    private List<DirectorDto> directors;
}