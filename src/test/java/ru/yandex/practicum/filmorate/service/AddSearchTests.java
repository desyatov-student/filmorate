package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.SearchMode;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate")
@Sql(value = {"/add-search/clear.sql", "/add-search/schema.sql", "/add-search/test-data-init.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = "/add-search/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class AddSearchTests {
    private final FilmService filmService;

    @Test
    public void searchByTitleTest() {
        //Given
        String query = "крад";
        List<SearchMode> modes = List.of(SearchMode.TITLE);

        //When
        List<FilmDto> results = filmService.search(query, modes);

        //Then
        assertThat(results).size().isEqualTo(2);
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "Крадущийся тигр, затаившийся дракон");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(2);

        assertThat(results.get(1))
                .hasFieldOrPropertyWithValue("name", "Крадущийся в ночи");
        assertThat(results.get(1).getDirectors()).size().isEqualTo(1);
    }

    @Test
    public void searchByTitleThatHasZeroDirectorsTest() {
        //Given
        String query = "lm4";
        List<SearchMode> modes = List.of(SearchMode.TITLE);

        //When
        List<FilmDto> results = filmService.search(query, modes);

        //Then
        assertThat(results).size().isEqualTo(1);
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "film4");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(0);
    }

    @Test
    public void searchByEmptyTitleTest() {
        //Given
        String query = "";
        List<SearchMode> modes = List.of(SearchMode.TITLE);

        //When
        List<FilmDto> results = filmService.search(query, modes);

        //Then
        assertThat(results).isEmpty();
    }

    @Test
    public void searchByDirectorTest() {
        //Given
        String query = "рад";
        List<SearchMode> modes = List.of(SearchMode.DIRECTOR);

        //When
        List<FilmDto> results = filmService.search(query, modes);

        //Then
        assertThat(results).size().isEqualTo(1);
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("name", "film5");
        assertThat(results.get(0).getDirectors()).size().isEqualTo(1);
        assertThat(results.get(0).getDirectors().get(0).getName()).isEqualTo("Крадович");
    }

    @Test
    public void searchByEmptyDirectorTest() {
        //Given
        String query = "";
        List<SearchMode> modes = List.of(SearchMode.DIRECTOR);

        //When
        List<FilmDto> results = filmService.search(query, modes);

        //Then
        assertThat(results).isEmpty();
    }

    @Test
    public void searchByTitleAndDirectorTest() {
        //Given
        String query = "КрАд";
        List<SearchMode> modes = List.of(SearchMode.TITLE, SearchMode.DIRECTOR);

        //When
        List<FilmDto> results = filmService.search(query, modes);

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
    public void searchByEmptyTitleAndDirectorTest() {
        //Given
        String query = "";
        List<SearchMode> modes = List.of(SearchMode.DIRECTOR, SearchMode.TITLE);

        //When
        List<FilmDto> results = filmService.search(query, modes);

        //Then
        assertThat(results).isEmpty();
    }
}
