package ru.practicum.ewmapp.exception.other;

import ru.practicum.ewmapp.exception.EwmException;

public class CommentsAreNotAllowedException extends EwmException {
    public CommentsAreNotAllowedException(String message) {
        super(message);
    }
}
