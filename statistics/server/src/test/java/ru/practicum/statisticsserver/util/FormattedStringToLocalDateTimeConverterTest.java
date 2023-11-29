package ru.practicum.statisticsserver.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class FormattedStringToLocalDateTimeConverterTest {
    FormattedStringToLocalDateTimeConverter converter = new FormattedStringToLocalDateTimeConverter();

    @Test
    public void converterConvertsFormattedString() {
        String source = "2023-01-01 01:01:01";
        Assertions.assertEquals(LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                converter.convert(source));
    }

    @Test
    public void conversionFailsWhenWrongFormat() {
        String source = "01-01-2023 01:01:01";
        Assertions.assertThrows(DateTimeParseException.class, () -> converter.convert(source));
    }
}
