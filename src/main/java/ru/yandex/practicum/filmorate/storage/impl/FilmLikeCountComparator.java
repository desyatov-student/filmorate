package ru.yandex.practicum.filmorate.storage.impl;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmLikeCountComparator implements Comparator<Film> {
    @Override
    public int compare(Film val1, Film val2) {
        return Integer.compare(val1.getLikes().size(), val2.getLikes().size());
    }
}
