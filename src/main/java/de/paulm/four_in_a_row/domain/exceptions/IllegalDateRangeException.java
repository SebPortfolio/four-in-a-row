package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class IllegalDateRangeException extends ApplicationException {
    public IllegalDateRangeException(Comparable<?> startDate, Comparable<?> endDate) {
        super(String.format("Enddatum %s liegt vor dem Startdatum %s", endDate, startDate),
                HttpStatus.BAD_REQUEST,
                "END_BEFORE_START");
    }
}
