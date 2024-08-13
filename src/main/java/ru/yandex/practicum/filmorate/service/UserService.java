package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapperImpl;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.mappers.UserMapperImpl;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userDbStorage;
    private final FilmStorage filmDbStorage;
    private final UserMapper userMapper = new UserMapperImpl();
    private final FeedStorage feedStorage;
    private final FilmMapper filmMapper = new FilmMapperImpl();

    public List<UserDto> getUsers() {
        return userDbStorage.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getById(Long userId) {
        User user = getUserById(userId, String.format("Пользователь с id = %d не найден", userId));
        return userMapper.toDto(user);
    }

    public User getUserById(Long userId) {
        return getUserById(userId, String.format("Пользователь с id = %d не найден", userId));
    }

    public UserDto create(NewUserRequest request) {

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        Optional<User> alreadyExistUser = userDbStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            log.error("Creating user is failed. email = {} exists", request.getEmail());
            throw new DuplicatedDataException("User with email = " + request.getEmail() + " exists");
        }

        updateUserNameIfNotExist(request, request);
        User user = userMapper.toUser(request);
        user = userDbStorage.save(user);
        log.info("Creating user is successful: {}", user);
        return userMapper.toDto(user);
    }

    public UserDto update(Long userId, UpdateUserRequest request) {
        if (userId == null) {
            log.error("Updating user is failed. user id is null");
            throw new ConditionsNotMetException("Id must be provided.");
        }

        User updatedUser = getUserById(userId);
        Optional<User> alreadyExistUser = userDbStorage.findByEmail(request.getEmail());
        if (alreadyExistUser.isPresent()) {
            log.error("Updating user is failed. email = {} exists", request.getEmail());
            throw new DuplicatedDataException("User with email = " + request.getEmail() + " exists");
        }

        userMapper.updateUser(updatedUser, request);
        updatedUser = userDbStorage.update(updatedUser);
        log.info("Updating user is successful: {}", updatedUser);
        return userMapper.toDto(updatedUser);
    }

    public void createFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getFriend(friendId);
        if (userDbStorage.hasFriend(user, friend)) {
            String message = String.format("Friend exists for userId = %d, friendId = %d", user.getId(), friend.getId());
            log.error(message);
            throw new DuplicatedDataException(message);
        }
        userDbStorage.saveFriend(user, friend);
        feedStorage.save(
                new FeedDto(
                        Instant.now().toEpochMilli(),
                        user.getId(),
                        EventType.FRIEND,
                        Operation.ADD,
                        friend.getId()));
    }

    public void removeFriend(Long id, Long friendId) {
        User user = getUserById(id);
        User friend = getFriend(friendId);
        userDbStorage.removeFriend(user, friend);
        feedStorage.save(
                new FeedDto(
                        Instant.now().toEpochMilli(),
                        user.getId(),
                        EventType.FRIEND,
                        Operation.REMOVE,
                        friend.getId()));
    }

    public void removeUser(Long id) {
        User user = getUserById(id);
        userDbStorage.removeUser(user);
    }

    public List<UserDto> getFriends(Long id) {
        User user = getUserById(id);
        return userDbStorage.getFriends(user).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getCommonFriends(Long id, Long otherId) {
        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        return userDbStorage.getCommonFriends(user, otherUser).stream()
                .map(userMapper::toDto)
                .toList();
    }

    private User getFriend(Long id) {
        return getUserById(id, String.format("Друг с id = %d не найден", id));
    }

    private User getUserById(Long id, String errorMessage) {
        return userDbStorage.findById(id)
                .orElseThrow(() -> {
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    private void updateUserNameIfNotExist(NewUserRequest target, NewUserRequest source) {
        if (source.getName() == null) {
            target.setName(source.getLogin());
        } else {
            target.setName(source.getName());
        }
    }

    public List<FilmDto> getRecommendations(Long userId) {
        User user = getUserById(userId);
        return filmDbStorage.findRecommendations(user.getId()).stream()
                .map(filmMapper::toDto)
                .toList();
    }
}
