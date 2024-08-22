package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PastOrNull;
import ru.yandex.practicum.filmorate.validation.ValidEmailOrNull;
import ru.yandex.practicum.filmorate.validation.ValidLoginOrNull;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private Long id;

    @ValidEmailOrNull
    private String email;

    @ValidLoginOrNull
    private String login;

    private String name;

    @PastOrNull
    private LocalDate birthday;
}