package de.paulm.four_in_a_row.domain.exceptions;

public class PlayerProfileNotFoundException extends RuntimeException {
    public PlayerProfileNotFoundException(Long id, String parameterName) {
        super("SpielerProfil mit " + parameterName + " " + id + " nicht gefunden.");
    }
}