package ru.practicum.enums;

import java.util.Optional;

public enum UserStateEvent {
    SEND_TO_REVIEW, CANCEL_REVIEW;
    public static Optional<UserStateEvent> from(String stringState) {
        for (UserStateEvent state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
