package ru.practicum.statisticsserver.endpointhit.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.statisticsserver.util.StatisticsServerValidator;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EndpointHitValidationTest {
    private final StatisticsServerValidator validator;
    private EndpointHit hit;

    @BeforeEach
    public void setup() {
        hit = new EndpointHit(null, "app", "/uri", "0.0.0.0", LocalDateTime.now());
    }

    @Test
    public void validatorWhenValidHitDoesNotThrow() {
        Assertions.assertDoesNotThrow(() -> validator.validate(hit));
    }

    @Test
    public void validatorWhenAppIsNullThrows() {
        hit.setApp(null);

        ConstraintViolationException e
                = Assertions.assertThrows(ConstraintViolationException.class, () -> validator.validate(hit));
        Assertions.assertTrue(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .anyMatch(str -> str.equals("app")));
    }

    @Test
    public void validatorWhenUriIsNullThrowsException() {
        hit.setUri(null);

        ConstraintViolationException e
                = Assertions.assertThrows(ConstraintViolationException.class, () -> validator.validate(hit));
        Assertions.assertTrue(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .anyMatch(str -> str.equals("uri")));
    }

    @Test
    public void validatorWhenIpIsNullThrowsException() {
        hit.setIp(null);

        ConstraintViolationException e
                = Assertions.assertThrows(ConstraintViolationException.class, () -> validator.validate(hit));
        Assertions.assertTrue(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .anyMatch(str -> str.equals("ip")));
    }

    @Test
    public void validatorWhenIpIsBadFormattedThrowsException() {
        hit.setIp("1.2.3");

        ConstraintViolationException e
                = Assertions.assertThrows(ConstraintViolationException.class, () -> validator.validate(hit));
        Assertions.assertTrue(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .anyMatch(str -> str.equals("ip")));
    }

    @Test
    public void validatorWhenTimestampIsNullThrowsException() {
        hit.setTimestamp(null);

        ConstraintViolationException e
                = Assertions.assertThrows(ConstraintViolationException.class, () -> validator.validate(hit));
        Assertions.assertTrue(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .anyMatch(str -> str.equals("timestamp")));
    }

    @Test
    public void validatorWhenTimestampIsFutureThrowsException() {
        hit.setTimestamp(LocalDateTime.now().plusHours(1));

        ConstraintViolationException e
                = Assertions.assertThrows(ConstraintViolationException.class, () -> validator.validate(hit));
        Assertions.assertTrue(e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .anyMatch(str -> str.equals("timestamp")));
    }
}
