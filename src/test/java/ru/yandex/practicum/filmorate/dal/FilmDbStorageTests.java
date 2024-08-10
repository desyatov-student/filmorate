package ru.yandex.practicum.filmorate.dal;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan
class FilmDbStorageTests {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @AllArgsConstructor
    static class DataForRecommendations {
        ArrayList<User> users;
        ArrayList<Film> films;
    }

    @Test
    public void testFindRecommendations_returnListOfFilms_UserHasRecommendations() {

        // Given
        DataForRecommendations dataForRecommendations = prepareDataForRecommendations();
        List<User> users = dataForRecommendations.users;
        List<Film> films = dataForRecommendations.films;

        // When
        List<Film> recommendations = filmStorage.findRecommendations(users.get(9).getId());

        // Then
        assertThat(List.of(films.get(3), films.get(4))).isEqualTo(recommendations);
    }

    private DataForRecommendations prepareDataForRecommendations() {
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Film> films = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            User user = new User(
                    null,
                    "email" + i + "@mail.ru",
                    "login" + i,
                    "name" + i,
                    LocalDate.of(1990, 1, 1)
            );
            users.add(userStorage.save(user));

            Film film = new Film(
                    null,
                    "name" + i,
                    "desc" + i,
                    LocalDate.of(1990, 1, 1),
                    60,
                    List.of(),
                    new Mpa(1L, "G"),
                    List.of()
            );

            films.add(filmStorage.save(film));
        }

        /*
        Фильмы под индексами 0, 1, 2 Лайкало большинство в том числе и Пользователь под индексом 9
        Фильмы под индексами 3 и 4 лайкали другие, которые лайкали и Фильмы под индексами 0, 1, 2
        Пользователь под индексом 9 не лайкал фильмы под индексами 3 и 4 поэтому они в рекомендациях
         */

        filmStorage.like(films.get(0), users.get(0).getId());
        filmStorage.like(films.get(1), users.get(0).getId());
        filmStorage.like(films.get(2), users.get(0).getId());
        filmStorage.like(films.get(3), users.get(0).getId());
        filmStorage.like(films.get(4), users.get(0).getId());

        filmStorage.like(films.get(1), users.get(1).getId());
        filmStorage.like(films.get(2), users.get(1).getId());
        filmStorage.like(films.get(3), users.get(1).getId());

        filmStorage.like(films.get(1), users.get(2).getId());
        filmStorage.like(films.get(2), users.get(2).getId());
        filmStorage.like(films.get(3), users.get(2).getId());

        filmStorage.like(films.get(0), users.get(4).getId());
        filmStorage.like(films.get(1), users.get(4).getId());
        filmStorage.like(films.get(2), users.get(4).getId());
        filmStorage.like(films.get(3), users.get(4).getId());

        filmStorage.like(films.get(2), users.get(5).getId());

        filmStorage.like(films.get(0), users.get(9).getId());
        filmStorage.like(films.get(1), users.get(9).getId());
        filmStorage.like(films.get(2), users.get(9).getId());

        filmStorage.like(films.get(2), users.get(10).getId());

        return new DataForRecommendations(users, films);
    }



    static Film getFilm() {
        ArrayList<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1L, "Комедия"));
        return Film.builder()
                .id(1L)
                .name("testFilm1")
                .description("testDescription")
                .releaseDate(LocalDate.of(2020, 11, 23))
                .duration(150)
                .mpa(new Mpa(1L, "G"))
                .genres(genres)
                .build();
    }

    @Test
    public void getPopular_shouldFindPopularFilmsWithCountParam() {
        Collection<Film> films = filmStorage.getPopular(2L, null, null);
        assertTrue(films.size() == 2);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());
    }

    @Test
    public void getPopular_shouldFindPopularFilmsWithGenreIdParam() {
        Collection<Film> films = filmStorage.getPopular(null, 1L, null);
        assertTrue(films.size() == 3);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());
    }

    @Test
    public void getPopular_shouldFindPopularFilmsWithYearParam() {
        Collection<Film> films = filmStorage.getPopular(null, null, 2001L);
        System.out.println(films);
        assertTrue(films.size() == 1);
        Film film = getFilm().toBuilder()
                .name("testFilm3")
                .releaseDate(LocalDate.of(2001, 11, 23))
                .id(3L)
                .build();
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(film);
    }

    @Test
    public void getPopular_shouldFindPopularFilmsWithAllParams() {
        Collection<Film> films = filmStorage.getPopular(10L, 1L, 2020L);
        assertTrue(films.size() == 2);
        assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());
    }

}