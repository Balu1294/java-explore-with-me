package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ViewStats {
    String app;  // Название сервиса
    String uri;  // URI сервиса
    Integer hits; // Количество просмотров
}
