package ru.yandex.practicum.filmorate.storage.dbstorage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User.UserBuilder builder = User.builder();

        builder.id(resultSet.getLong("id"));
        builder.email(resultSet.getString("email"));
        builder.login(resultSet.getString("login"));
        builder.name(resultSet.getString("name"));
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        builder.birthday(birthday);

        return builder.build();
    }
}