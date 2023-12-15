package ru.practicum.ewmapp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class StringToEnumConverterFactoryTest {
    private StringToEnumConverterFactory factory = new StringToEnumConverterFactory();

    @Test
    void getConverter() {
        Converter<String, TestEnum> testEnumConverter = factory.getConverter(TestEnum.class);
        Assertions.assertEquals(TestEnum.TEST, testEnumConverter.convert("TEST"));
        Assertions.assertEquals(TestEnum.ENUM, testEnumConverter.convert(" eNUm  "));
    }
}