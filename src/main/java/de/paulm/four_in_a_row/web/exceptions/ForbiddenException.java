package de.paulm.four_in_a_row.web.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class ForbiddenException extends ApplicationException {
    public ForbiddenException(String msg) {
        super(msg, HttpStatus.FORBIDDEN, "ACCESS_FORBIDDEN");
    }
}
