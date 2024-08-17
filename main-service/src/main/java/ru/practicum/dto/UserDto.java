package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class UserDto {
    Integer id;
    String email;
    String name;
}
