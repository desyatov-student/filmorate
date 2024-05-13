package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdentifierGenerator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private final IdentifierGenerator identifierGenerator = new IdentifierGenerator();

    @GetMapping
    public Collection<User> findAll() {
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (isEmailExist(user.getEmail())) {
            log.error("Creating user is failed. email = {} exists", user.getEmail());
            throw new DuplicatedDataException("User with email = " + user.getEmail() + " exists");
        }
        // формируем дополнительные данные
        user.setId(identifierGenerator.getNextId());
        updateUserNameIfNotExist(user, user);
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.info("Creating user is successful: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            log.error("Updating user is failed. user id is null {}", newUser);
            throw new ConditionsNotMetException("Id must be provided.");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("User = {} is not found", newUser);
            throw new NotFoundException("User with id = " + newUser.getId() + " is not found");
        }

        User oldUser = users.get(newUser.getId());
        String newEmail = newUser.getEmail();

        if (isUserExist(newUser)) {
            log.error("Updating user is failed. email = {} exists", newUser.getEmail());
            throw new DuplicatedDataException("User with email = " + newUser.getEmail() + " exists");
        }
        oldUser.setEmail(newEmail);
        oldUser.setLogin(newUser.getLogin());
        updateUserNameIfNotExist(oldUser, newUser);
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Updating user is successful: {}", oldUser);
        return oldUser;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("User validation errors {} ", errors, ex);
        return errors;
    }

    private boolean isEmailExist(String email) {
        return getUserByEmail(email).isPresent();
    }

    private Optional<User> getUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    private boolean isUserExist(User user) {
        Optional<User> userOpt = getUserByEmail(user.getEmail());
        return userOpt.filter(value -> !value.getId().equals(user.getId())).isPresent();
    }

    private void updateUserNameIfNotExist(User target, User source) {
        if (source.getName() == null) {
            target.setName(source.getLogin());
        } else {
            target.setName(source.getName());
        }
    }
}
