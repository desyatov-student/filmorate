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
    public void testFindPopularFilms() {

        Collection<Film> films = filmStorage.getPopular(2L, null, null);
        assertThat(films.size() == 2);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());

        films = filmStorage.getPopular(10L, 1L, null);
        assertThat(films.size() == 3);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());

        films = filmStorage.getPopular(10L, null, 2001L);
        assertThat(films.size() == 1);
        Film film = getFilm().toBuilder()
                .releaseDate(LocalDate.of(2001, 11, 23))
                .id(3L)
                .build();
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(film);

        films = filmStorage.getPopular(10L, 1L, 2020L);
        assertThat(films.size() == 2);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());
    }

}