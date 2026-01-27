package de.paulm.four_in_a_row.domain.exceptions;

public class ColumnFullException extends RuntimeException {
    public ColumnFullException(int spalte) {
        super("Die Spalte " + spalte + " ist voll.");
    }

}
