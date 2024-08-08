package ru.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class NewEventDto {
    String annotation;
    Integer categoryId;
    String description;
    LocalDateTime eventDate;
    LocationDto location;
    Boolean isPaid;
    Integer participantLimit;
    Boolean isRequestModeration;
    String title;

}
