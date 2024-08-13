package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

@Data
@AllArgsConstructor
public class FeedDto {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}
