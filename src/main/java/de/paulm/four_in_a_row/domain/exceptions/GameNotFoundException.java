package de.paulm.four_in_a_row.domain.exceptions;

import de.paulm.four_in_a_row.web.exceptions.advice.ResourceNotFoundException;

public class GameNotFoundException extends ResourceNotFoundException {
    public GameNotFoundException(Long id) {
        super("Spiel mit ID " + id + " nicht gefunden.", "GAME_NOT_FOUND", "id");
    }
}
