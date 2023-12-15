package ru.practicum.ewmapp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class TwoHoursOrMoreFromNowValidatorTest {
    private TwoHoursOrMoreFromNowValidator validator = new TwoHoursOrMoreFromNowValidator();

    @Test
    void isValid() {
        LocalDateTime validTime = LocalDateTime.now().plusHours(2L).plusSeconds(2);
        LocalDateTime invalidTime = LocalDateTime.now().plusHours(2L);

        Assertions.assertTrue(validator.isValid(validTime, null));
        Assertions.assertFalse(validator.isValid(invalidTime, null));
    }
}