package de.paulm.four_in_a_row.domain.exceptions;

import de.paulm.four_in_a_row.web.exceptions.advice.ResourceNotFoundException;

public class UserSessionNotFoundException extends ResourceNotFoundException {
    public UserSessionNotFoundException(String refreshToken) {
        super("Keine UserSession mit diesem refreshToken: " + refreshToken,
                "SESSION_NOT_FOUND",
                "refreshToken");
    }
}
