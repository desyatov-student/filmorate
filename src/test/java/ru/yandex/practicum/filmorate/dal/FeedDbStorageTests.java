package ru.yandex.practicum.filmorate.dal;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan
public class FeedDbStorageTests {
    private final FeedDbStorage feedDbStorage;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    public void before() {
        userDbStorage.save(new User(
                1L,
                "user1@yandex.ru",
                "user1",
                "name1",
                LocalDate.of(2000, 7, 15)));

        userDbStorage.save(new User(
                2L,
                "user2@yandex.ru",
                "user2",
                "name2",
                LocalDate.of(2000, 7, 15)));
    }

    @Test
    public void testGetFeedsList() {
        feedDbStorage.save(new FeedDto(
                Instant.now().toEpochMilli(),
                1L,
                "FRIEND",
                "ADD",
                2L));

        feedDbStorage.save(new FeedDto(
                Instant.now().toEpochMilli(),
                1L,
                "FRIEND",
                "REMOVE",
                2L));

        List<Feed> feeds = feedDbStorage.getFeed(1L);

        assertThat(feeds.size()).isEqualTo(2);
        assertThat(feeds.get(0).getEventType()).isEqualTo("FRIEND");
        assertThat(feeds.get(0).getOperation()).isEqualTo("ADD");
        assertThat(feeds.get(1).getEventType()).isEqualTo("FRIEND");
        assertThat(feeds.get(1).getOperation()).isEqualTo("REMOVE");
    }
}
