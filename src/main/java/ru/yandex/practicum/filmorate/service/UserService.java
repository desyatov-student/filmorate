package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public Optional<User> findById(Long userId) {
        return storage.findById(userId);
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public User addFriend(Long id, Long friendId) {
        User user = getUser(id);
        User friend = getFriend(friendId);
        return storage.addFriend(user, friend);
    }

    public void removeFriend(Long id, Long friendId) {
        User user = getUser(id);
        User friend = getFriend(friendId);
        storage.removeFriend(user, friend);
    }

    public List<User> getFriends(Long id) {
        User user = getUser(id);
        return storage.getFriends(user);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getUser(id);
        User otherUser = getUser(otherId);
        return storage.getCommonFriends(user, otherUser);
    }

    private User getUser(Long id) {
        return getUser(id, String.format("Пользователь с id = %d не найден", id));
    }

    private User getFriend(Long id) {
        return getUser(id, String.format("Друг с id = %d не найден", id));
    }

    private User getUser(Long id, String errorMessage) {
        return storage.findById(id)
                .orElseThrow(() -> new ConditionsNotMetException(errorMessage));
    }
}
