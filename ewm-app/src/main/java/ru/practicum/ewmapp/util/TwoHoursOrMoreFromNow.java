package ru.practicum.ewmapp.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = TwoHoursOrMoreFromNowValidator.class)
public @interface TwoHoursOrMoreFromNow {
    String message() default "New event has to begin two hours or more from present moment.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
