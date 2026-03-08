package de.paulm.four_in_a_row.domain.exceptions;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.domain.security.BanReason;
import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class OtherBanActiveException extends ApplicationException {
    public OtherBanActiveException(BanReason activeBanReason) {
        super(String.format("Es ist bereits ein Bann wegen %s aktiv", activeBanReason),
                HttpStatus.BAD_REQUEST,
                "OTHER_BAN_ACTIVE");
    }
}
