package de.paulm.four_in_a_row.domain.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Ungültige Anmeldedaten");
    }
}
