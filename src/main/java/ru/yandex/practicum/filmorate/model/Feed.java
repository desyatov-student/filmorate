package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Feed {
    private Long id;
    private LocalDateTime timestamp;
    private Long userId;
    private String eventType;
    private Long eventId;
    private Long entityId;
}
