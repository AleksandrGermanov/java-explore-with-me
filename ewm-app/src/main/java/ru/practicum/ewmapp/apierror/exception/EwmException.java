package ru.practicum.ewmapp.apierror.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EwmException extends RuntimeException{
    private String reason;

    public EwmException(String message){
        super(message);
    }
}
