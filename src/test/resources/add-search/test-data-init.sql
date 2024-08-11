INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES
    ('Крадущийся тигр, затаившийся дракон', 'film1_desc', '1999-05-03', 100, 1),
    ('Крадущийся в ночи', 'film3_desc', '2012-07-02', 121, 3),
    ('film3', 'film3_desc', '2012-07-02', 121, 3),
    ('film4', 'film4_desc', '2011-08-01', 90, 4),
    ('film5', 'film5_desc', '2008-09-11', 130, 5);

INSERT INTO users(email, login, name, birthday) VALUES
    ('username1@gmail.com', 'user_login1', 'user1', '1894-09-11'),
    ('username2@gmail.com', 'user_login2', 'user2', '1991-05-01'),
    ('username3@gmail.com', 'user_login3', 'user3', '1999-06-02'),
    ('username4@gmail.com', 'user_login4', 'user4', '1985-08-13'),
    ('username5@gmail.com', 'user_login5', 'user5', '1922-09-22');

INSERT INTO film_likes(film_id, user_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 1), (2, 4),
    (3, 1), (3, 3), (3, 4),
    (4, 1), (4, 4),
    (5, 5);

INSERT INTO film_genres(film_id, genre_id) VALUES
(1, 5), (2, 4), (3, 3), (4, 2), (5, 1);

INSERT INTO directors(name) VALUES
('Крадович'), ('Director2'), ('Director3'), ('Director4'), ('Director5');

INSERT INTO film_directors(film_id, director_id) VALUES
(1, 5), (1, 4), (2, 2), (3, 4), (5, 1);