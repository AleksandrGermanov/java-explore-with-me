package ru.practicum.ewmapp.exception.other;

import ru.practicum.ewmapp.exception.EwmException;

public class ModerationNotRequiredException extends EwmException {
    public ModerationNotRequiredException(String message) {
        super(message);
    }
}
