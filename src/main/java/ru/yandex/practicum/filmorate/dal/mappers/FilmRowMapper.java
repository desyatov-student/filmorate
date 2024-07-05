package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

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
        builder.genreId(resultSet.getLong("genre_id"));
        builder.ratingId(resultSet.getLong("rating_id"));
        return builder.build();
    }
}