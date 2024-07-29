package ru.practicum;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@NoArgsConstructor
public class ViewStats {
    String app;  // Название сервиса
    String uri;  // URI сервиса
    Integer hits; // Количество просмотров
}
