package de.paulm.four_in_a_row.domain.exceptions;

public class IllegalUsernameException extends RuntimeException {
    public IllegalUsernameException(String username, String cause) {
        super("Der Benutzername '" + username + "' ist ungültig. Grund: " + cause);
    }
}
