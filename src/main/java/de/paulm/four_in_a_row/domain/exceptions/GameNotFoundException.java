package de.paulm.four_in_a_row.domain.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(Long id) {
        super("Spiel mit ID " + id + " nicht gefunden.");
    }
}
