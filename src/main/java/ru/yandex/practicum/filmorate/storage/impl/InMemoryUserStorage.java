package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdentifierGenerator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<User>> friends = new HashMap<>();
    private final IdentifierGenerator identifierGenerator;

    @Autowired
    public InMemoryUserStorage(IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
    }

    public Collection<User> findAll() {
        return users.values().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(userId));
    }

    public User create(User user) {
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

    public User update(User newUser) {
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

    @Override
    public User addFriend(User user, User friend) {
        Set<User> userFriends = getFriendSet(user);
        userFriends.add(friend);
        return friend;
    }

    @Override
    public void removeFriend(User user, User friend) {
        Set<User> userFriends = getFriendSet(user);
        userFriends.remove(friend);
    }

    @Override
    public List<User> getFriends(User user) {
        return getFriendSet(user).stream().toList();
    }

    @Override
    public List<User> getCommonFriends(User user, User otherUser) {
        Set<User> userFriends = getFriendSet(user);
        Set<User> otherUserFriends = getFriendSet(otherUser);
        userFriends.retainAll(otherUserFriends);
        return userFriends.stream().toList();
    }

    private Set<User> getFriendSet(User user) {
        return friends.computeIfAbsent(user.getId(), (k) -> new HashSet<>());
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
