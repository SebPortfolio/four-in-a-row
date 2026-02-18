package de.paulm.four_in_a_row.domain.exceptions;

public class IllegalDisplayNameException extends RuntimeException {
    public IllegalDisplayNameException(String displayName, String cause) {
        super("Der Anzeigename '" + displayName + "' ist ungültig. Grund: " + cause);
    }
}
