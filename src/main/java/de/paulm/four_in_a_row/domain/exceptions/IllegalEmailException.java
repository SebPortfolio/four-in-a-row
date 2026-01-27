package de.paulm.four_in_a_row.domain.exceptions;

public class IllegalEmailException extends RuntimeException {
    public IllegalEmailException(String email, String cause) {
        super("Die E-Mail-Adresse '" + email + "' ist ungültig. Grund: " + cause);
    }

}
