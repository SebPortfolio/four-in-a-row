package de.paulm.four_in_a_row.domain.exceptions;

public class UserSessionNotFoundException extends RuntimeException {
    public UserSessionNotFoundException(String refreshToken) {
        super("Keine UserSession mit diesem refreshToken: " + refreshToken);
    }
}
