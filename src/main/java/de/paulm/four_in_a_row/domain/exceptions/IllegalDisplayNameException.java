package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class IllegalDisplayNameException extends ApplicationException {
    public IllegalDisplayNameException(String displayName, String cause) {
        super("Der Anzeigename '" + displayName + "' ist ungültig. Grund: " + cause, HttpStatus.BAD_REQUEST,
                "INVALID_DISPLAY_NAME");
    }
}
