package de.paulm.four_in_a_row.domain.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class IllegalEmailException extends ApplicationException {
    public IllegalEmailException(String email, String cause) {
        super("Die E-Mail-Adresse '" + email + "' ist ungültig. Grund: " + cause, HttpStatus.BAD_REQUEST,
                "INVALID_EMAIL", "email", createDetailsMap(email));
    }

    private static Map<String, Object> createDetailsMap(String email) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        return payload;
    }
}
