package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTests {


    Validator validator;

    public UserTests() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void validate_Success_userIsValid() {
        // Given
        User user = createUser();

        // When

        Set<ConstraintViolation<User>> validates = validator.validate(user);

        // Then

        assertTrue(validates.isEmpty());
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_emailIsNotValid(String email, List<String> expectedErrors) {
        // Given
        User user = createUser();
        user.setEmail(email);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(user)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(expectedErrors, errors);
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_loginIsNotValid(String login, List<String> expectedErrors) {
        // Given
        User user = createUser();
        user.setLogin(login);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(user)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(
                expectedErrors.stream().sorted().toList(),
                errors.stream().sorted().toList()
        );
    }

    @ParameterizedTest
    @MethodSource
    void validate_Failed_birthdayIsNotValid(LocalDate birthday, List<String> expectedErrors) {
        // Given
        User user = createUser();
        user.setBirthday(birthday);

        // When

        List<String> errors =  new ArrayList<>(validator.validate(user)).stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        // Then

        assertEquals(expectedErrors, errors);
    }

    private User createUser() {
        return new User(null, "email@mail.ru", "login", null, LocalDate.of(1990, 1, 1));
    }

    private static Stream<Arguments> validate_Failed_emailIsNotValid() {
        return Stream.of(
                Arguments.of("email.email.ru", List.of("Должен быть корректный e-mail адрес")),
                Arguments.of("", List.of("Не должно быть пустым")),
                Arguments.of(null, List.of("Не должно быть пустым"))
        );
    }

    private static Stream<Arguments> validate_Failed_loginIsNotValid() {
        return Stream.of(
                Arguments.of("log in", List.of("Должен быть без пробелов")),
                Arguments.of("", List.of("Не должно быть пустым", "Должен быть без пробелов")),
                Arguments.of(null, List.of("Не должно быть пустым"))
        );
    }

    private static Stream<Arguments> validate_Failed_birthdayIsNotValid() {
        return Stream.of(
                Arguments.of(LocalDate.of(3025, 1, 1), List.of("Дата рождения должна быть меньше текущей даты")),
                Arguments.of(null, List.of("Обязательное поле"))
        );
    }
}
