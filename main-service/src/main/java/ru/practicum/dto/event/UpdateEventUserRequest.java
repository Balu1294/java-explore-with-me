package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.enums.UserStateEvent;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {
    private UserStateEvent stateAction;
}
