package de.paulm.four_in_a_row.domain.exceptions;

public class PlayerProfileNotFoundException extends RuntimeException {
    public PlayerProfileNotFoundException(Long id) {
        super("SpielerProfil mit ID " + id + " nicht gefunden.");
    }
}