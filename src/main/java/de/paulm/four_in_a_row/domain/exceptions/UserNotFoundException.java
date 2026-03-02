package de.paulm.four_in_a_row.domain.exceptions;

import de.paulm.four_in_a_row.web.exceptions.advice.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("Ungültige Anmeldedaten", "USER_NOT_FOUND", null);
    }

    public UserNotFoundException(Long userId) {
        super("Kein User mit der userId: " + userId, "USER_NOT_FOUND", "userId");
    }

    public UserNotFoundException(String email) {
        super("Kein User mit der email: " + email, "USER_NOT_FOUND", "email");
    }
}
