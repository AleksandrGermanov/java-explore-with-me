package ru.practicum.ewmapp.apierror.exception;

public class RequestIdMismatchException extends EwmException{
    public RequestIdMismatchException(String message) {
        super(message);
    }
}
