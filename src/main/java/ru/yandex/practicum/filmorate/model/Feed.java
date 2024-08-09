package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Feed {
    private Long id;
    private Long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long eventId;
    private Long entityId;
}
