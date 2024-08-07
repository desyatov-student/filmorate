package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FeedDto {
    private LocalDateTime timestamp;
    private long userId;
    private String eventType;
    private long eventId;
    private long entityId;
}
