package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT f.*,
            m.NAME AS mpa_name,
            ARRAY_AGG(DISTINCT g.ID) AS genre_ids,
            ARRAY_AGG(DISTINCT g.NAME) AS genre_names
            FROM FILMS f
            LEFT JOIN film_genres fg ON fg.FILM_ID = f.id
            LEFT JOIN genres g ON g.ID = fg.GENRE_ID
            LEFT JOIN mpa m ON m.ID = f.MPA_ID
            GROUP BY f.ID;
            """;
    private static final String FIND_POPULAR_QUERY = """
            SELECT film.* ,
            m.NAME AS mpa_name,
            ARRAY_AGG(DISTINCT g.ID) AS genre_ids,
            ARRAY_AGG(DISTINCT g.NAME) AS genre_names
            FROM (SELECT f.*, COUNT (fl.user_id) AS count_like
            FROM films f
            LEFT JOIN film_likes fl  ON fl.film_id = f.id
            GROUP BY f.id
            ) AS film
            LEFT JOIN film_genres fg ON fg.FILM_ID = film.id
            LEFT JOIN genres g ON g.ID = fg.GENRE_ID
            LEFT JOIN mpa m ON m.ID = film.MPA_ID
            %S
            GROUP BY film.ID
            ORDER BY count_like DESC
                        """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*,
            m.NAME AS mpa_name,
            ARRAY_AGG(g.ID) AS genre_ids,
            ARRAY_AGG(g.NAME) AS genre_names
            FROM FILMS f
            LEFT JOIN film_genres fg ON fg.FILM_ID = f.id
            LEFT JOIN genres g ON g.ID = fg.GENRE_ID
            LEFT JOIN mpa m ON m.ID = f.MPA_ID
            WHERE f.ID = ?
            GROUP BY f.ID;
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
    private static final String DELETE_FILM_QUERY = "DELETE films WHERE id = ?;";

    // recommendations
    private static final String FIND_RECOMMENDATIONS_QUERY = """
            WITH recommends_films_ids AS (
                WITH fl AS (SELECT DISTINCT USER_ID, FILM_ID FROM FILM_LIKES WHERE USER_ID = ?)
                SELECT
                FL2.USER_ID,
                fl2.FILM_ID
                FROM (
                    SELECT
                    fl2.USER_ID,
                    COUNT(DISTINCT fl2.FILM_ID) FILM_LIKES_count,
                    COUNT(DISTINCT fl3.FILM_ID) FILMS_HAVE_NOT_LIKED_YET_count
                    FROM fl
                    JOIN film_likes fl2 ON fl2.FILM_ID = fl.FILM_ID AND FL.USER_ID != fl2.USER_ID
                    JOIN (SELECT DISTINCT f.USER_ID, f.FILM_ID FROM film_likes f JOIN fl ON f.FILM_ID != fl.FILM_ID) fl3 ON FL2.USER_ID = fl3.USER_ID
                    GROUP BY fl2.USER_ID
                    ORDER BY FILM_LIKES_count DESC
                    LIMIT 1
                ) ui
                JOIN film_likes fl2 ON fl2.USER_ID = ui.USER_ID
                WHERE fl2.FILM_ID NOT IN (SELECT DISTINCT FILM_ID FROM fl)
            )
            SELECT f.*,
            m.NAME AS mpa_name,
            ARRAY_AGG(DISTINCT g.ID) AS genre_ids,
            ARRAY_AGG(DISTINCT g.NAME) AS genre_names
            FROM recommends_films_ids rfi
            JOIN films f ON rfi.FILM_ID = f.id
            LEFT JOIN film_genres fg ON fg.FILM_ID = f.id
            LEFT JOIN genres g ON g.ID = fg.GENRE_ID
            LEFT JOIN mpa m ON m.ID = f.MPA_ID
            GROUP BY f.ID;
            """;

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
                film.getMpa().getId()
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
                film.getMpa().getId(),
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
    public void removeFilm(Film film) {
        delete(DELETE_FILM_QUERY, film.getId());
    }

    @Override
    public List<Film> getPopular(Long count, Long genreId, Long year) {
        String findQuery;
        if (genreId != null && year != null) {
            findQuery = String.format(FIND_POPULAR_QUERY,
                    "WHERE  film.ID IN (SELECT FILM_ID FROM film_genres WHERE GENRE_ID = " + genreId + ") " +
                            "AND YEAR (film.RELEASE_DATE) = " + year);
        } else if (genreId != null) {
            findQuery = String.format(FIND_POPULAR_QUERY,
                    "WHERE  film.ID IN (SELECT FILM_ID FROM film_genres WHERE GENRE_ID = " + genreId + ")");
        } else if (year != null) {
            findQuery = String.format(FIND_POPULAR_QUERY,
                    "WHERE YEAR (film.RELEASE_DATE) = " + year);
        } else {
            findQuery = String.format(FIND_POPULAR_QUERY, " ");

        }
        if (count != null) {
            findQuery = findQuery + "LIMIT " + count;
        }
        return findMany(findQuery);
    }

    @Override
    public List<Film> findRecommendations(Long userId) {
        return findMany(FIND_RECOMMENDATIONS_QUERY, userId);
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
        for (Genre genre : film.getGenres()) {
            insert(INSERT_FILM_GENRES_QUERY, film.getId(), genre.getId());
        }
    }
}
