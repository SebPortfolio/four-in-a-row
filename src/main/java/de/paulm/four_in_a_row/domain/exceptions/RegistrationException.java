package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class RegistrationException extends ApplicationException {
    public RegistrationException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST, "REGISTRATION_FAILURE");
    }
}
