package ru.practicum.ewmapp.exception.other;

import ru.practicum.ewmapp.exception.EwmException;

public class RequestAlreadyExistsException extends EwmException {
    public RequestAlreadyExistsException(String message) {
        super(message);
    }
}
