package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan
class FilmDbStorageTests {

    private final FilmDbStorage filmStorage;

    static Film getFilm() {
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1L, "Комедия"));
        return Film.builder()
                .id(1L)
                .name("testFilm")
                .description("testDescription")
                .releaseDate(LocalDate.of(2020, 11, 23))
                .duration(150)
                .mpa(new Mpa(1L, "G"))
                .genres(genres)
                .build();
    }

    @Test
    public void testFindCommonFilms() {

        Collection<Film> films = filmStorage.getCommon(1L, 2L);
        assertThat(films.size() == 1);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());

        filmStorage.like(filmStorage.findFilmById(2L).get(), 1L);
        films = filmStorage.getCommon(1L, 2L);
        assertThat(films.size() == 2);



    }

}

