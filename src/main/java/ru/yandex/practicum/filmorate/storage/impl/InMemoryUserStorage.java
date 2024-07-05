package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdentifierGenerator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<User>> friends = new HashMap<>();
    private final IdentifierGenerator identifierGenerator = new IdentifierGenerator();

    public List<User> findAll() {
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

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public void saveFriend(User user, User friend) {
        Set<User> userFriends = getFriendSet(user);
        userFriends.add(friend);
        Set<User> friendFriends = getFriendSet(friend);
        friendFriends.add(user);
    }

    @Override
    public boolean hasFriend(User user, User friend) {
        return false;
    }

    @Override
    public boolean removeFriend(User user, User friend) {
        Set<User> userFriends = getFriendSet(user);
        userFriends.remove(friend);
        Set<User> friendFriends = getFriendSet(friend);
        friendFriends.remove(user);
        return true;
    }

    @Override
    public List<User> getFriends(User user) {
        return getFriendSet(user).stream().toList();
    }

    @Override
    public List<User> getCommonFriends(User user, User otherId) {
        Set<User> userFriends = getFriendSet(user);
        Set<User> otherUserFriends = getFriendSet(otherId);
        userFriends.retainAll(otherUserFriends);
        return userFriends.stream().toList();
    }

    private Set<User> getFriendSet(User user) {
        return friends.computeIfAbsent(user.getId(), (k) -> new HashSet<>());
    }

    private boolean isEmailExist(String email) {
        return findByEmail(email).isPresent();
    }

    private boolean isUserExist(User user) {
        Optional<User> userOpt = findByEmail(user.getEmail());
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
