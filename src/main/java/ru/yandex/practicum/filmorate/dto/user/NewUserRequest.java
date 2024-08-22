package ru.yandex.practicum.filmorate.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PastAndNotNull;
import ru.yandex.practicum.filmorate.validation.ValidEmailAndNotBlank;
import ru.yandex.practicum.filmorate.validation.ValidLoginAndNotBlank;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @ValidEmailAndNotBlank
    private String email;

    @ValidLoginAndNotBlank
    private String login;

    private String name;

    @PastAndNotNull
    private LocalDate birthday;
}
