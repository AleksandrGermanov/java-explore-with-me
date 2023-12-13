package ru.practicum.ewmapp.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import ru.practicum.commondtolib.JacksonLocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    @JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
    private LocalDateTime timestamp;
}
