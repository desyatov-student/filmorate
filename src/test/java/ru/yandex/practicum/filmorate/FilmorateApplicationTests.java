package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class})
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;

	@Test
	public void testFindUserById_returnUser_UserExists() {

		// Given
		User initialUser = createUser();
		Long userId = userStorage.save(initialUser).getId();

		// When
		Optional<User> userOptional = userStorage.findById(userId);

		// Then
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> {
							assertThat(user).hasFieldOrPropertyWithValue("id", userId);
							assertThat(user).hasFieldOrPropertyWithValue("email", initialUser.getEmail());
							assertThat(user).hasFieldOrPropertyWithValue("login", initialUser.getLogin());
							assertThat(user).hasFieldOrPropertyWithValue("name", initialUser.getName());
							assertThat(user).hasFieldOrPropertyWithValue("birthday", initialUser.getBirthday());
				});

	}

	private User createUser() {
		return new User(null, "email@mail.ru", "login", "name", LocalDate.of(1990, 1, 1));
	}
}
