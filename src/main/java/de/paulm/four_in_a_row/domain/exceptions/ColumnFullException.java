package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class ColumnFullException extends ApplicationException {
    public ColumnFullException(int spalte) {
        super("Die Spalte " + spalte + " ist voll.", HttpStatus.BAD_REQUEST, "COLUMN_FULL");
    }

}
