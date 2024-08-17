package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class UserShortDto {
    Integer id;
    String name;
}
