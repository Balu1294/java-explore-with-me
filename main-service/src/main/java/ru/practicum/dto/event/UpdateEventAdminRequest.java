package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.enums.AdminStateEvent;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private AdminStateEvent stateAction;
}
