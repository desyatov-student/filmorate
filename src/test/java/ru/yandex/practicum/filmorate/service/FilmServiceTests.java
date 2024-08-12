package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FilmService.class, FilmMapperImpl.class})
public class FilmServiceTests {

    @MockBean
    private FilmStorage filmStorage;
    @MockBean
    private GenreStorage genreStorage;
    @MockBean
    private DirectorStorage directorStorage;
    @MockBean
    private MpaStorage mpaStorage;
    @MockBean
    private UserService userService;
    @Autowired
    private FilmService filmService;
    @Autowired
    private FilmMapper filmMapper;

    @Test
    void getFilms_ReturnListOfFilms_NoError() throws IOException, InterruptedException {

        // Given
        List<Film> expectedFilms = List.of(
                new Film(1L, "name", "desc", LocalDate.now(), 30, List.of(), new Mpa(1L, "R1"), List.of()),
                new Film(2L, "name2", "desc2", LocalDate.now(), 40, List.of(), new Mpa(1L, "R1"), List.of())
        );
        when(filmStorage.findAll()).thenReturn(expectedFilms);

        // When
        List<FilmDto> actualFilms = filmService.getFilms();

        // Then
        assertEquals(expectedFilms.stream().map(filmMapper::toDto).toList(), actualFilms);
        Mockito.verify(filmStorage).findAll();
        Mockito.verifyNoMoreInteractions(filmStorage);
    }
}

