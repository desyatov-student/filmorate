package ru.yandex.practicum.filmorate.converter;

import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.model.SearchMode;

public class StringToEnumConverter implements Converter<String, SearchMode> {
    @Override
    public SearchMode convert(String source) {
            return SearchMode.valueOf(source.toUpperCase());
    }
}
