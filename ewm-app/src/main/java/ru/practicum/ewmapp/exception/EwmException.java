package ru.practicum.ewmapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EwmException extends RuntimeException {
    public EwmException(String message) {
        super(message);
    }
}
