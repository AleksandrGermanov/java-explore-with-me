package ru.practicum.ewmapp.exception.other;

import ru.practicum.ewmapp.exception.EwmException;

public class ParticipantLimitReachedException extends EwmException {
    public ParticipantLimitReachedException(String message) {
        super(message);
    }
}
