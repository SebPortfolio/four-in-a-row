package de.paulm.four_in_a_row.domain.exceptions;

public class SpielerProfilNotFoundException extends RuntimeException {
    public SpielerProfilNotFoundException(Long id) {
        super("SpielerProfil mit ID " + id + " nicht gefunden.");
    }
}