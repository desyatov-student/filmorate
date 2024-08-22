MERGE INTO genres KEY(ID)
VALUES
	(1, 'Комедия'),
	(2, 'Драма'),
	(3, 'Мультфильм'),
	(4, 'Триллер'),
	(5, 'Документальный'),
	(6, 'Боевик');

MERGE INTO mpa KEY(ID)
VALUES
	(1, 'G'),
	(2, 'PG'),
	(3, 'PG-13'),
	(4, 'R'),
	(5, 'NC-17');

INSERT INTO users (name,email,login,birthday)
VALUES
    ('testName1','testEmail1','testLogin1','2020-11-23'),
    ('testName2','testEmail2','testLogin2','2020-11-23'),
    ('testName3','testEmail3','testLogin3','2020-11-23');

INSERT INTO films (name,description,release_date,duration,mpa_id)
VALUES
    ('testFilm1','testDescription', '2020-11-23',150, 1),
    ('testFilm2','testDescription', '2020-11-23',150, 1),
    ('testFilm3','testDescription', '2001-11-23',150, 1),
    ('testFilm4','testDescription', '2020-11-23',150, 1);

INSERT INTO film_genres (film_id,genre_id) VALUES
    (1,1),
    (2,1),
    (2,3),
    (3,1),
    (4,3);

INSERT INTO film_likes (film_id,user_id) VALUES
    (1,1),
    (1,2),
    (2,2);

INSERT INTO directors (name)
VALUES
    ('director1'),
    ('director2'),
    ('director3 surname3'),
    ('director4');

INSERT INTO film_directors (film_id, director_id)
VALUES
    (1,1),
    (2,2),
    (2,3),
    (3,1);