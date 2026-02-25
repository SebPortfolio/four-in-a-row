package de.paulm.four_in_a_row.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Ungültige Anmeldedaten");
    }

    public UserNotFoundException(Long userId) {
        super("Kein User mit der userId: " + userId);
    }

    public UserNotFoundException(String email) {
        super("Kein User mit der email: " + email);
    }
}
