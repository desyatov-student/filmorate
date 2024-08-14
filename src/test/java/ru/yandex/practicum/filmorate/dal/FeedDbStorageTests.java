package ru.yandex.practicum.filmorate.dal;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbstorage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.UserDbStorage;

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

    @Test
    public void testGetFriendFeedsList() {
        //Given
        userDbStorage.save(new User(
                1L,
                "user1@yandex.ru",
                "user1",
                "name1",
                LocalDate.of(2000, 7, 15)));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.FRIEND,
                Operation.ADD,
                2L));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.FRIEND,
                Operation.REMOVE,
                2L));
        //When
        List<Feed> feeds = feedDbStorage.getFeed(1L);

        //Then
        assertThat(feeds).hasSize(2);
        assertThat(feeds.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(feeds.get(0).getOperation()).isEqualTo(Operation.ADD);
        assertThat(feeds.get(1).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(feeds.get(1).getOperation()).isEqualTo(Operation.REMOVE);
    }

    @Test
    public void testGetLikeFeedList() {
        //Given
        userDbStorage.save(new User(
                1L,
                "user1@yandex.ru",
                "user1",
                "name1",
                LocalDate.of(2000, 7, 15)));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.LIKE,
                Operation.ADD,
                2L
        ));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.LIKE,
                Operation.REMOVE,
                2L
        ));

        //When
        List<Feed> feeds = feedDbStorage.getFeed(1L);

        //Then
        assertThat(feeds).hasSize(2);
        assertThat(feeds.get(0).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(feeds.get(0).getOperation()).isEqualTo(Operation.ADD);
        assertThat(feeds.get(1).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(feeds.get(1).getOperation()).isEqualTo(Operation.REMOVE);
    }

    @Test
    public void testGetReviewFeedList() {
        //Given
        userDbStorage.save(new User(
                1L,
                "user1@yandex.ru",
                "user1",
                "name1",
                LocalDate.of(2000, 7, 15)));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.REVIEW,
                Operation.ADD,
                2L
        ));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.REVIEW,
                Operation.UPDATE,
                2L
        ));

        feedDbStorage.save(new Feed(
                null,
                Instant.now().toEpochMilli(),
                1L,
                EventType.REVIEW,
                Operation.REMOVE,
                2L
        ));

        //When
        List<Feed> feeds = feedDbStorage.getFeed(1L);

        //Then
        assertThat(feeds).hasSize(3);
        assertThat(feeds.get(0).getEventType()).isEqualTo(EventType.REVIEW);
        assertThat(feeds.get(0).getOperation()).isEqualTo(Operation.ADD);
        assertThat(feeds.get(1).getEventType()).isEqualTo(EventType.REVIEW);
        assertThat(feeds.get(1).getOperation()).isEqualTo(Operation.UPDATE);
        assertThat(feeds.get(2).getEventType()).isEqualTo(EventType.REVIEW);
        assertThat(feeds.get(2).getOperation()).isEqualTo(Operation.REMOVE);
    }
}
