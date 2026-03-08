package de.paulm.four_in_a_row.domain.security.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.paulm.four_in_a_row.domain.security.validators.LocalDateRangeValidator;
import de.paulm.four_in_a_row.domain.security.validators.LocalDateTimeRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {
        LocalDateTimeRangeValidator.class,
        LocalDateRangeValidator.class
})
@Documented
public @interface ValidDateRange {
    String message() default "{validation.invalid_date_range}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
