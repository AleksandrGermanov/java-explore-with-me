package ru.practicum.ewmapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmapp.exception.mismatch.EventDateMismatchException;
import ru.practicum.ewmapp.exception.mismatch.MismatchException;
import ru.practicum.ewmapp.exception.notfound.NotFoundException;
import ru.practicum.ewmapp.exception.other.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        String reason = "No data found in repository.";
        log.debug("Handling the exception : {}, {}.", e.getClass().getName(), e.getMessage());
        return new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                reason, HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler(value = {MismatchException.class,
            ParticipantLimitReachedException.class,
            RequestAlreadyExistsException.class,
            ModerationNotRequiredException.class,
            DataIntegrityViolationException.class,
            CommentsAreNotAllowedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(Exception e) {
        String reason = "Requirements for the operation were not met.";
        log.debug("Handling the exception : {}, {}.", e.getClass().getName(), e.getMessage());
        return new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                reason, HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String reason = "Request parameters failed the validation.";
        List<String> errors = !e.getBindingResult().hasErrors()
                ? Collections.emptyList()
                : e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage() + " (was "
                        + error.getRejectedValue() + ")")
                .collect(Collectors.toList());
        List<String> objectErrors = e.getBindingResult().getGlobalErrors().stream()
                .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        errors.addAll(objectErrors);
        String message = "Request parameters failed the validation: " + String.join(" | ", errors);
        log.debug("Handling the exception : {}, {}.", e.getClass().getName(), message);
        return new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                reason, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolation(ConstraintViolationException e) {
        List<String> violations = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        log.debug("Request parameters failed the validation. {}", String.join(" | ", violations));
        String reason = "Request parameters failed the validation.";
        String message = "Request parameters failed the validation. " + String.join(" | ", violations);
        log.debug("Handling the exception : {}, {}.", e.getClass().getName(), message);
        return new ApiError(List.of(e.getClass().getName()), message,
                reason, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
            StartIsAfterEndException.class,
            EventDateMismatchException.class,
            TransactionSystemException.class,
            RequestParametersMisusageException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(Exception e) {
        String reason = "One or several request parameters did not pass the validation.";
        log.debug("Handling the exception : {}, {}.", e.getClass().getName(), e.getMessage());
        return new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                reason, HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUnexpected(Exception e) {
        String reason = "Unexpected error has occurred.";
        log.warn("Unexpected exception : {}, {}.", e.getClass().getName(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return new ApiError(List.of(e.getClass().getName()), e.getMessage(),
                reason, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }
}
