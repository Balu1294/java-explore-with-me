package ru.practicum.enums;

import java.util.Optional;

public enum AdminStateEvent {
    PUBLISH_EVENT, REJECT_EVENT;
    public static Optional<AdminStateEvent> from(String stringState) {
        for (AdminStateEvent state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
