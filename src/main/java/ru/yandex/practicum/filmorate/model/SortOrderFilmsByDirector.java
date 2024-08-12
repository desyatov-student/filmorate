package ru.yandex.practicum.filmorate.model;

public enum SortOrderFilmsByDirector {
    YEAR, LIKES;

    public static SortOrderFilmsByDirector from(String order) {
        switch (order.toLowerCase()) {
            case "year":
                return YEAR;
            case "likes":
                return LIKES;
            default: return null;
        }
    }
}