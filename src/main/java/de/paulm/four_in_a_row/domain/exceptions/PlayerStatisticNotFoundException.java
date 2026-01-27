package de.paulm.four_in_a_row.domain.exceptions;

public class PlayerStatisticNotFoundException extends RuntimeException {
    public PlayerStatisticNotFoundException(Long id) {
        super("SpielerStatistik mit ID " + id + " nicht gefunden.");
    }
}