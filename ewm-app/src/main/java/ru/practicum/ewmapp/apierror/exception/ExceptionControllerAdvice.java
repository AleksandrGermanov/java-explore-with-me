package ru.practicum.ewmapp.apierror.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmapp.apierror.ApiError;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(EwmException e){
        String reason = "No data found in repository.";
        return new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                reason, HttpStatus.NOT_FOUND, LocalDateTime.now());
    }
}
