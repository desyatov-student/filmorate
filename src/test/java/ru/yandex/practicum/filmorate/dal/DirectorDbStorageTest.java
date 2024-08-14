package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.dbstorage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({DirectorDbStorage.class, DirectorRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {
    private static final long TEST_DIRECTOR_ID = 1L;
    private static final long COUNT_OF_ELEMENTS = 4;

    private final DirectorDbStorage directorDbStorage;

    static Director getTestDirector() {
        return Director.builder()
                .id(TEST_DIRECTOR_ID)
                .name("director1")
                .build();
    }

    @Test
    void findById_returnsEmptyOptionalInCaseOfNonExisting() {
        // Given
        // test-data.sql

        // When
        Optional<Director> director = directorDbStorage.findById(COUNT_OF_ELEMENTS + 1);

        // Then
        assertThat(director).isEmpty();
    }

    @Test
    void findById_returnsCorrectDirectorInCaseOfExisting() {
        // Given
        // test-data.sql

        // When
        Optional<Director> director = directorDbStorage.findById(TEST_DIRECTOR_ID);

        // Then
        assertThat(director)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getTestDirector());

    }

    @Test
    void findAll_returnsCorrectNumberOfElements() {
        // Given
        // test-data.sql

        // When
        List<Director> directors = directorDbStorage.findAll();

        // Then
        assertThat(directors)
                .hasSize((int) COUNT_OF_ELEMENTS);
    }

    @Test
    void save_createsDirectorInDatabase() {
        // Given
        // test-data.sql

        // When
        Director createDirector = directorDbStorage.save(getTestDirector());

        // Then
        assertThat(directorDbStorage.findById(createDirector.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(getTestDirector());
    }

    @Test
    void update_updatesDirectorInDatabase() {
        // Given
        // test-data.sql

        // When
        Director updateDirector = getTestDirector().toBuilder()
                .id(TEST_DIRECTOR_ID)
                .name("another test name")
                .build();

        directorDbStorage.update(updateDirector);

        // Then
        assertThat(directorDbStorage.findById(updateDirector.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(updateDirector);
    }

    @Test
    void deleteById_deletesDirectorById() {
        // Given
        // test-data.sql

        // When
        directorDbStorage.deleteById(TEST_DIRECTOR_ID);

        // Then
        assertThat(directorDbStorage.findById(TEST_DIRECTOR_ID)).isEmpty();
    }
}