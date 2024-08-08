package ru.practicum.dto;

import lombok.*;
import ru.practicum.enums.AdminStateEvent;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private AdminStateEvent stateAction;
}
