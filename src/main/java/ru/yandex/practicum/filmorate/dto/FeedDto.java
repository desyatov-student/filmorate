package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedDto {
    private Long timestamp;
    private long userId;
    private String eventType;
    private String operation;
    private long eventId;
    private long entityId;
}
