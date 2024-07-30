package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {
    String app;  // Название сервиса
    String uri;  // URI сервиса
    Integer hits; // Количество просмотров
}
