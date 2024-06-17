# java-filmorate
Template repository for Filmorate project.

![filmorate-erd.png](db_diagram/filmorate-erd.png)

Получить все фильмы:

```sql
SELECT f.name,
       g.name,
       r.name
FROM film AS f
INNER JOIN genre AS g ON g.id=f.genre_id
INNER JOIN rating AS r ON r.id=f.ratin_id
```