package ru.yandex.practicum.filmorate.model;

public enum SearchMode {
    WITHOUT_PARAM,
    DIRECTOR_AND_TITLE,
    DIRECTOR,
    TITLE;

    public static SearchMode from(String order) {
        switch (order.toLowerCase()) {
            case "director,title", "title,director":
                return DIRECTOR_AND_TITLE;
            case "director":
                return DIRECTOR;
            case "title":
                return TITLE;
            case "":
                return WITHOUT_PARAM;
            default:
                return null;
        }
    }
}
