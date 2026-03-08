package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class BanUserMismatchException extends ApplicationException {
    public BanUserMismatchException(Long banId, Long userId) {
        super("Ban #" + banId + " gethört nicht zum User #" + userId, HttpStatus.BAD_REQUEST, "BAN_USER_MISMATCH");
    }
}
