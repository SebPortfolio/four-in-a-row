package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class BanNotActiveException extends ApplicationException {
    public BanNotActiveException() {
        super("Bann kann nicht mehr bearbeitet werden, da er bereits inaktiv ist",
                HttpStatus.BAD_REQUEST,
                "BAN_NOT_ACTIVE");
    }
}
