package ru.practicum;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHit {
    Integer id;
    String app; // Идентификатор сервиса для которого записывается информация
    String uri; // URI для которого был осуществлен запрос
    String ip;  // IP-адрес пользователя, осуществившего запрос
    LocalDateTime timestamp; // Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
}
