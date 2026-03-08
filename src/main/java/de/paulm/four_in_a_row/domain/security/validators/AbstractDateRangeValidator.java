package de.paulm.four_in_a_row.domain.security.validators;

import de.paulm.four_in_a_row.domain.security.annotations.ValidDateRange;
import de.paulm.four_in_a_row.domain.security.interfaces.DateTimeRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public abstract class AbstractDateRangeValidator<T extends Comparable<? super T>>
        implements ConstraintValidator<ValidDateRange, DateTimeRange<T>> {

    @Override
    public boolean isValid(DateTimeRange<T> object, ConstraintValidatorContext context) {
        if (object == null || object.getStartAt() == null || object.getEndAt() == null) {
            return true;
        }

        boolean isValid = object.getStartAt().compareTo(object.getEndAt()) <= 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endAt")
                    .addConstraintViolation();
        }
        return isValid;
    }
}