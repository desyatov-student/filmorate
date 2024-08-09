package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

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
            ResultSet resultSetGenres = resultSet.getArray("genres").getResultSet();
            ArrayList<Genre> arrayList = new ArrayList<>();
            while (resultSetGenres.next()) {
                Object[] directorFields = (Object[]) resultSetGenres.getArray("Value").getArray();
                Long genreId = Long.valueOf(String.valueOf(directorFields[0]));
                String genreName = String.valueOf(directorFields[1]);
                arrayList.add(new Genre(genreId, genreName));
            }
            builder.genres(arrayList);
        } catch (Exception e) {
            builder.genres(new ArrayList<>());
        }
        Mpa map = new Mpa(
                resultSet.getLong("mpa_id"),
                resultSet.getString("mpa_name")
        );
        builder.mpa(map);
        try {
            ResultSet resultSetDirectors = resultSet.getArray("directors").getResultSet();
            ArrayList<Director> arrayList = new ArrayList<>();
            while (resultSetDirectors.next()) {
                Object[] directorFields = (Object[]) resultSetDirectors.getArray("Value").getArray();
                Long directorId = Long.valueOf(String.valueOf(directorFields[0]));
                String directorName = String.valueOf(directorFields[1]);
                arrayList.add(new Director(directorId, directorName));
            }
            builder.directors(arrayList);
        } catch (Exception e) {
            builder.directors(new ArrayList<>());
        }
        return builder.build();
    }
}