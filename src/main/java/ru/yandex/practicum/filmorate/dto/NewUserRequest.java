package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Не должно быть пустым")
    @Email(message = "Должен быть корректный e-mail адрес")
    private String email;

    @NotBlank(message = "Не должно быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Должен быть без пробелов")
    private String login;

    private String name;

    @NotNull(message = "Обязательное поле")
    @Past(message = "Дата рождения должна быть меньше текущей даты")
    private LocalDate birthday;
}
