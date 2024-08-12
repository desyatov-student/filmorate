package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTests {

    Validator validator;

    public FilmTests() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validate_Success_filmIsValid() {
        // Given
        NewFilmRequest film = createNewFilmRequest();

        // When

        Set<ConstraintViolation<NewFilmRequest>> validates = validator.validate(film);

        // Then

        assertTrue(validates.isEmpty());
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_nameIsNotValid(String name, List<String> expectedErrors) {
        // Given
        NewFilmRequest film = createNewFilmRequest();
        film.setName(name);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(film)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(expectedErrors, errors);
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_descriptionIsNotValid(String description, List<String> expectedErrors) {
        // Given
        NewFilmRequest film = createNewFilmRequest();
        film.setDescription(description);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(film)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(expectedErrors, errors);
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_releaseDateIsNotValid(LocalDate releaseDate, List<String> expectedErrors) {
        // Given
        NewFilmRequest film = createNewFilmRequest();
        film.setReleaseDate(releaseDate);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(film)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(expectedErrors, errors);
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_durationIsNotValid(Integer duration, List<String> expectedErrors) {
        // Given
        NewFilmRequest film = createNewFilmRequest();
        film.setDuration(duration);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(film)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(expectedErrors, errors);
    }

    private NewFilmRequest createNewFilmRequest() {
        return new NewFilmRequest(null, "film name",
                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tatio",
                LocalDate.of(1895, 12, 28), 60, new LinkedHashSet<>(List.of(new GenreDto(1L, "name"))), new MpaDto(1L, "name"), new LinkedHashSet<>(List.of()));
    }

    private static Stream<Arguments> validate_Failed_nameIsNotValid() {
        return Stream.of(
                Arguments.of("", List.of("Не должно быть пустым")),
                Arguments.of(null, List.of("Не должно быть пустым"))
        );
    }

    private static Stream<Arguments> validate_Failed_descriptionIsNotValid() {
        return Stream.of(
                Arguments.of(
                        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tatio1",
                        List.of("must be less 200 characters")
                ),
                Arguments.of(null, List.of("must be less 200 characters"))
        );
    }

    private static Stream<Arguments> validate_Failed_releaseDateIsNotValid() {
        return Stream.of(
                Arguments.of(LocalDate.of(1895, 1, 1), List.of("must be in past")),
                Arguments.of(null, List.of("must be in past"))
        );
    }

    private static Stream<Arguments> validate_Failed_durationIsNotValid() {
        return Stream.of(
                Arguments.of(-1, List.of("must be positive and not null")),
                Arguments.of(0, List.of("must be positive and not null")),
                Arguments.of(null, List.of("must be positive and not null"))
        );
    }
}