package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@ToString
public class ViewStatsRequest {
    @Builder.Default
    LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    @Builder.Default
    LocalDateTime end = LocalDateTime.now();
    List<String> uris;
    Boolean unique;
    String app;
}
