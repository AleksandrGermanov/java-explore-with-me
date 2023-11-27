package ru.practicum.endpointhitclient;

public class EndpointHitClientException extends RuntimeException {
    public EndpointHitClientException(String message) {
        super(message);
    }

    public EndpointHitClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
