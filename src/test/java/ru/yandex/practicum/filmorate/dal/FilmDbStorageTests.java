package ru.yandex.practicum.filmorate.dal;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.SortOrderFilmsByDirector;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                .directors(List.of(new Director(1L, "director1")))
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

    @Test
    public void getCommon_shouldFindCommonFilms() {
        Collection<Film> films = filmStorage.getCommon(1L, 2L);
        Assertions.assertThat(films.size() == 1);
        Assertions.assertThat(films.stream().findFirst())
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(getFilm());
    }

    @Test
    public void getCommon_afterLikeCountCommonFilmsWillIncrease() {
        Collection<Film> films = filmStorage.getCommon(1L, 2L);
        Assertions.assertThat(films.size() == 1);

        filmStorage.like(filmStorage.findFilmById(2L).get(), 1L);

        films = filmStorage.getCommon(1L, 2L);
        Assertions.assertThat(films.size() == 2);
    }

    @Test
    public void getDirectorFilms_throwsExceptionWhenWrongSortingParameter() {

        // When
        Throwable thrown = catchThrowable(() -> {
            filmStorage.getDirectorFilms(1L, null);
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getDirectorFilms_returnEmptyListOfFilmsWhenDirectorHasNoFilms() {

        // Given
        // test-data.sql

        // When
        List<Film> directorFilms = filmStorage.getDirectorFilms(5L, SortOrderFilmsByDirector.YEAR);

        // Then
        assertThat(directorFilms)
                .isEmpty();
    }

    @Test
    public void getDirectorFilms_returnListOfFilmsSortedByYear() {

        // Given
        // test-data.sql

        // When
        List<Film> directorFilms = filmStorage.getDirectorFilms(1L, SortOrderFilmsByDirector.YEAR);

        // Then
        assertThat(directorFilms)
                .hasSize(2)
                .first()
                .returns(3L, Film::getId);
    }

    @Test
    public void getDirectorFilms_returnListOfFilmsSortedByLikes() {

        // Given
        // test-data.sql

        // When
        List<Film> directorFilms = filmStorage.getDirectorFilms(1L, SortOrderFilmsByDirector.LIKES);

        // Then
        assertThat(directorFilms)
                .hasSize(2)
                .first()
                .returns(1L, Film::getId);
    }

    //add-search tests
    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByTitleTest() {
        //Given
        String query = "крад";
        boolean searchByTitle = true;
        boolean searchByDirector = false;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).size().isEqualTo(2);
        AssertionsForClassTypes.assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "Крадущийся тигр, затаившийся дракон");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(2);

        AssertionsForClassTypes.assertThat(results.get(1))
                .hasFieldOrPropertyWithValue("name", "Крадущийся в ночи");
        assertThat(results.get(1).getDirectors()).size().isEqualTo(1);
    }


    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByTitleThatHasZeroDirectorsTest() {
        //Given
        String query = "lm4";
        boolean searchByTitle = true;
        boolean searchByDirector = false;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).size().isEqualTo(1);
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "film4");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(0);
    }

    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByEmptyTitleTest() {
        //Given
        String query = "";
        boolean searchByTitle = true;
        boolean searchByDirector = false;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).isEmpty();
    }

    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByDirectorTest() {
        //Given
        String query = "рад";
        boolean searchByTitle = false;
        boolean searchByDirector = true;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).size().isEqualTo(1);
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "film5");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(1);
        assertThat(results.get(0).getDirectors().get(0).getName()).isEqualTo("Крадович");
    }

    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByEmptyDirectorTest() {
        //Given
        String query = "";
        boolean searchByTitle = false;
        boolean searchByDirector = true;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).isEmpty();
    }

    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByTitleAndDirectorTest() {
        //Given
        String query = "КрАд";
        boolean searchByTitle = true;
        boolean searchByDirector = true;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).size().isEqualTo(3);
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "Крадущийся тигр, затаившийся дракон");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(2);

        assertThat(results.get(1))
                .hasFieldOrPropertyWithValue("name", "Крадущийся в ночи");
        assertThat(results.get(1).getDirectors()).size().isEqualTo(1);

        assertThat(results.get(2))
                .hasFieldOrPropertyWithValue("name", "film5");
        assertThat(results.get(2).getDirectors()).size().isEqualTo(1);
        assertThat(results.get(2).getDirectors().get(0).getName()).isEqualTo("Крадович");
    }

    @Test
    @Sql(value = {"/add-search/clear.sql", "/add-search/test-data-init.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void searchByEmptyTitleAndDirectorTest() {
        //Given
        String query = "";
        boolean searchByTitle = true;
        boolean searchByDirector = true;

        //When
        List<Film> results = filmStorage.search(query, searchByTitle, searchByDirector);

        //Then
        assertThat(results).isEmpty();
    }
}
