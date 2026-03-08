package de.paulm.four_in_a_row.domain.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;

public class IllegalDisplayNameException extends ApplicationException {
    public IllegalDisplayNameException(String displayName, String cause) {
        super("Der Anzeigename '" + displayName + "' ist ungültig. Grund: " + cause, HttpStatus.BAD_REQUEST,
                "INVALID_DISPLAY_NAME", "displayName", createDetailsMap(displayName));
    }

    private static Map<String, Object> createDetailsMap(String displayName) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("displayName", displayName);
        return payload;
    }
}
