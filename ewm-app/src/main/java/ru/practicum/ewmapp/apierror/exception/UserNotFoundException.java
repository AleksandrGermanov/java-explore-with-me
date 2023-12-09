package ru.practicum.ewmapp.apierror.exception;

public class UserNotFoundException extends EwmException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
