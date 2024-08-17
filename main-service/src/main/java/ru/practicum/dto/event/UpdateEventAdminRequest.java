package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.enums.AdminStateEvent;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private AdminStateEvent stateAction;
}
