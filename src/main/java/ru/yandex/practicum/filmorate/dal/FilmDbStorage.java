package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT f.*,
            ARRAY_AGG(fg.GENRE_ID) AS genres
            FROM FILMS f
            LEFT JOIN film_genres fg ON fg.FILM_ID = f.id
            GROUP BY f.ID;
            """;
    private static final String FIND_POPULAR_QUERY = """
            SELECT f.*,
            ARRAY_AGG(fg.GENRE_ID) AS genres,
            count(fl.ID) AS count
            FROM FILMS f
            JOIN film_genres fg ON fg.FILM_ID = f.id
            JOIN film_likes fl ON fl.FILM_ID = f.id
            GROUP BY f.ID
            ORDER BY count DESC
            LIMIT ?;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*,
            ARRAY_AGG(fg.GENRE_ID) AS genres
            FROM FILMS f
            LEFT JOIN film_genres fg ON fg.FILM_ID = f.id
            WHERE f.ID = ?;
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO films
            (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_QUERY = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?;
            """;

    // film_genres
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE film_genres WHERE film_id = ?;";
    private static final String INSERT_FILM_GENRES_QUERY = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?);
            """;

    // film_likes
    private static final String FIND_FILM_LIKE_QUERY = "SELECT * FROM film_likes WHERE film_id = ? AND user_id = ?;";
    private static final String INSERT_FILM_LIKES_QUERY = """
            INSERT INTO film_likes (film_id, user_id)
            VALUES (?, ?);
            """;
    private static final String DELETE_FILM_LIKES_QUERY = "DELETE film_likes WHERE film_id = ? AND user_id = ?;";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    @Override
    public Film save(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()
        );
        film.setId(id);
        saveFilmGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getId()
        );
        saveFilmGenres(film);
        return film;
    }

    @Override
    public void like(Film film, Long userId) {
        insert(INSERT_FILM_LIKES_QUERY, film.getId(), userId);
    }

    @Override
    public boolean hasLike(Film film, Long userId) {
        List<Map<String, Object>> mapList = this.jdbc.queryForList(FIND_FILM_LIKE_QUERY, film.getId(), userId);
        return !mapList.isEmpty();
    }

    @Override
    public void removeLike(Film film, Long userId) {
        delete(DELETE_FILM_LIKES_QUERY, film.getId(), userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        if (film.getGenres().isEmpty()) {
            delete(DELETE_FILM_GENRES_QUERY, film.getId());
            return;
        }
        delete(DELETE_FILM_GENRES_QUERY, film.getId());
        for (Long genreId : film.getGenres()) {
            insert(INSERT_FILM_GENRES_QUERY, film.getId(), genreId);
        }
    }
}
