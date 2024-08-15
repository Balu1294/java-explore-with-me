package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    Integer id;
    Set<Integer> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}
