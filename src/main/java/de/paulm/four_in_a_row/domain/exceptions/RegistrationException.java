package de.paulm.four_in_a_row.domain.exceptions;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String msg) {
        super(msg);
    }
}
