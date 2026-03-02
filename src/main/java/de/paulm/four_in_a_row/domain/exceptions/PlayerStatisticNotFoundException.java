package de.paulm.four_in_a_row.domain.exceptions;

import de.paulm.four_in_a_row.web.exceptions.advice.ResourceNotFoundException;

public class PlayerStatisticNotFoundException extends ResourceNotFoundException {
    public PlayerStatisticNotFoundException(Long id) {
        super("SpielerStatistik mit ID " + id + " nicht gefunden.",
                "PLAYER_STATISTIC_NOT_FOUND",
                "id");
    }
}