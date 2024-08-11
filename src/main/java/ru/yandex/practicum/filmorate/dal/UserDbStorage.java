package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            " VALUES (?, ?, ?, ?);";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

    // Relationships
    private static final String FIND_FRIEND_QUERY = "SELECT * FROM friends WHERE USER_ID = ? AND FRIEND_ID = ?;";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO " +
            "friends (user_id, friend_id) " +
            "VALUES (?, ?);";
    private static final String DELETE_FRIEND_QUERY = "DELETE friends WHERE USER_ID = ? AND FRIEND_ID = ?;";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?;";
    private static final String FIND_FRIENDS_QUERY = """
            SELECT u.* FROM friends r
            JOIN users u ON u.ID = r.FRIEND_ID
            WHERE r.USER_ID = ?;""";
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT u.* FROM friends r
            JOIN users u ON u.ID = r.FRIEND_ID
            WHERE r.USER_ID = ? AND r.FRIEND_ID in
            (SELECT FRIEND_ID FROM friends WHERE USER_ID = ?);""";


    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> findById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void saveFriend(User user, User friend) {
        insert(
                INSERT_FRIEND_QUERY,
                user.getId(),
                friend.getId()
        );
    }

    @Override
    public boolean hasFriend(User user, User friend) {
        List<Map<String, Object>> mapList = this.jdbc.queryForList(FIND_FRIEND_QUERY, user.getId(), friend.getId());
        return !mapList.isEmpty();
    }

    @Override
    public boolean removeFriend(User user, User friend) {
        return delete(DELETE_FRIEND_QUERY, user.getId(), friend.getId());
    }

    @Override
    public boolean removeUser(User user) {
        return delete(DELETE_USER_QUERY, user.getId());
    }

    @Override
    public List<User> getFriends(User user) {
        return findMany(FIND_FRIENDS_QUERY, user.getId());
    }

    @Override
    public List<User> getCommonFriends(User user, User otherId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, user.getId(), otherId.getId());
    }
}