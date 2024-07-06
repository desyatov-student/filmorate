package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film.FilmBuilder builder = Film.builder();
        builder.id(resultSet.getLong("id"));
        builder.name(resultSet.getString("name"));
        builder.description(resultSet.getString("description"));
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        builder.releaseDate(releaseDate);
        builder.duration(resultSet.getInt("duration"));
        try {
            Object[] objects = (Object[]) resultSet.getArray("genres").getArray();
            Set<Long> set = Arrays.stream(objects)
                    .map(String::valueOf)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            builder.genres(set);
        } catch (Exception e) {
            builder.genres(new HashSet<>());
        }
        builder.mpa(resultSet.getLong("mpa_id"));
        return builder.build();
    }
}