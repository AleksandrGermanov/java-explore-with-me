package ru.practicum.ewmapp.exception.other;

import ru.practicum.ewmapp.exception.EwmException;

public class StartIsAfterEndException extends EwmException {
    public StartIsAfterEndException(String message) {
        super(message);
    }
}
