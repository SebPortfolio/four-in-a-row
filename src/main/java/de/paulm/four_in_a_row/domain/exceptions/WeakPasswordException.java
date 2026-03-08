package de.paulm.four_in_a_row.domain.exceptions;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class WeakPasswordException extends ApplicationException {

    public WeakPasswordException(List<String> missingRequirements) {
        super(
                "Das Passwort ist zu schwach",
                HttpStatus.BAD_REQUEST,
                "WEAK_PASSWORD",
                "password",
                Map.of("missingRequirements", missingRequirements));
    }
}
