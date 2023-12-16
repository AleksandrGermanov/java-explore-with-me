package ru.practicum.ewmapp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class FormattedStringToLocalDateTimeConverterTest {
    private FormattedStringToLocalDateTimeConverter converter = new FormattedStringToLocalDateTimeConverter();

    @Test
    void convert() {
        LocalDateTime testLdt = LocalDateTime.of(1111, 11, 11, 11, 11, 11);
        String toConvert = "1111-11-11 11:11:11";
        Assertions.assertEquals(testLdt, converter.convert(toConvert));
    }
}