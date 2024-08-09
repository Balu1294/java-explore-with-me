package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.enums.Status;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private Status status;
}
