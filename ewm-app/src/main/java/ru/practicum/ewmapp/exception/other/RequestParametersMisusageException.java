package ru.practicum.ewmapp.exception.other;

import ru.practicum.ewmapp.exception.EwmException;

public class RequestParametersMisusageException extends EwmException {
    public RequestParametersMisusageException(String message) {
        super(message);
    }
}
