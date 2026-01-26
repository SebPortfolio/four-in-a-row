package de.paulm.four_in_a_row.domain.exceptions;

public class SpielerStatistikNotFoundException extends RuntimeException {
    public SpielerStatistikNotFoundException(Long id) {
        super("SpielerStatistik mit ID " + id + " nicht gefunden.");
    }
}