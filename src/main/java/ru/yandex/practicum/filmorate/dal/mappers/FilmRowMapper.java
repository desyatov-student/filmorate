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
            Object[] genreIds = (Object[]) resultSet.getArray("genre_ids").getArray();
            Object[] genreNames = (Object[]) resultSet.getArray("genre_names").getArray();
            ArrayList<Genre> arrayList = new ArrayList<>(genreNames.length);
            for (int i = 0; i < genreIds.length; i++) {
                Long genreId = Long.valueOf(String.valueOf(genreIds[i]));
                String genreName = String.valueOf(genreNames[i]);
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
            Object[] directorIds = (Object[]) resultSet.getArray("director_ids").getArray();
            Object[] directorNames = (Object[]) resultSet.getArray("director_names").getArray();
            ArrayList<Director> arrayList = new ArrayList<>(directorIds.length);
            for (int i = 0; i < directorIds.length; i++) {
                Long genreId = Long.valueOf(String.valueOf(directorIds[i]));
                String genreName = String.valueOf(directorNames[i]);
                arrayList.add(new Director(genreId, genreName));
            }
            builder.directors(arrayList);
        } catch (Exception e) {
            builder.directors(new ArrayList<>());
        }
        return builder.build();
    }
}