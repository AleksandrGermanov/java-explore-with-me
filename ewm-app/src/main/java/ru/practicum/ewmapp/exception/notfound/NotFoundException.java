package ru.practicum.ewmapp.exception.notfound;

import ru.practicum.ewmapp.exception.EwmException;

public abstract class NotFoundException extends EwmException {
    public NotFoundException(String message) {
        super(message);
    }
}
