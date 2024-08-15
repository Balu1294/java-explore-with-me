package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    Double lat;
    @Min(-180)
    @Max(180)
    @NotNull
    Double lon;
}
