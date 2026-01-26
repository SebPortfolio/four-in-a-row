package de.paulm.four_in_a_row.domain.exceptions;

public class SpalteVollException extends RuntimeException {
    public SpalteVollException(int spalte) {
        super("Die Spalte " + spalte + " ist voll.");
    }

}
