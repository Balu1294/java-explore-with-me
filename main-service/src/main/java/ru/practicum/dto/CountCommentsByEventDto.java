package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CountCommentsByEventDto {
    Integer eventId;
    Long countComments;
}