package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    User save(User user);

    User update(User newUser);

    void saveFriend(User user, User friend);

    boolean hasFriend(User user, User friend);

    boolean removeFriend(User user, User friend);

    boolean removeUser(User user);

    List<User> getFriends(User user);

    List<User> getCommonFriends(User user, User otherId);
}