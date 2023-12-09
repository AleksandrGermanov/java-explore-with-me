package ru.practicum.ewmapp.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class TwoHoursOrMoreFromNowValidator implements ConstraintValidator<TwoHoursOrMoreFromNow, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime time, ConstraintValidatorContext constraintValidatorContext) {
        return time.isAfter(LocalDateTime.now().plusHours(2L));
    }
}
