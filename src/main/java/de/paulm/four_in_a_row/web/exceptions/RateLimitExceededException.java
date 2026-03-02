package de.paulm.four_in_a_row.web.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;

import de.paulm.four_in_a_row.web.exceptions.advice.ApplicationException;
import lombok.Getter;

@Getter
public class RateLimitExceededException extends ApplicationException {
    private final static String errorCode = "RATE_LIMIT_EXCEEDED";

    public RateLimitExceededException(String message) {
        this(message, 60);
    }

    public RateLimitExceededException(String message, int retryAfterSeconds) {
        super(message,
                HttpStatus.TOO_MANY_REQUESTS,
                errorCode,
                null,
                Map.of("retry_after_seconds", retryAfterSeconds));
    }
}
