package ru.practicum.ewmapp.exception.notfound;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
