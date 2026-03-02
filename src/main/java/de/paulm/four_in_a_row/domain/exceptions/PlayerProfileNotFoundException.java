package de.paulm.four_in_a_row.domain.exceptions;

import de.paulm.four_in_a_row.web.exceptions.advice.ResourceNotFoundException;

public class PlayerProfileNotFoundException extends ResourceNotFoundException {
    public PlayerProfileNotFoundException(Long id, String parameterName) {
        super("SpielerProfil mit " + parameterName + " " + id + " nicht gefunden.",
                "PLAYER_PROFILE_NOT_FOUND",
                parameterName);
    }
}