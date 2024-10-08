package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.enums.RequestStatus;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private Set<Integer> requestIds;
    private RequestStatus status;
}
