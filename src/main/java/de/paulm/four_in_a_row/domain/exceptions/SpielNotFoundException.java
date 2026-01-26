package de.paulm.four_in_a_row.domain.exceptions;

public class SpielNotFoundException extends RuntimeException {
    public SpielNotFoundException(Long id) {
        super("Spiel mit ID " + id + " nicht gefunden.");
    }
}
