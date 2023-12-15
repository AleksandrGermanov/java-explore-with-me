package ru.practicum.statisticsserver.util;

public class StartIsAfterEndException extends RuntimeException {
    public StartIsAfterEndException(String message) {
        super(message);
    }
}
