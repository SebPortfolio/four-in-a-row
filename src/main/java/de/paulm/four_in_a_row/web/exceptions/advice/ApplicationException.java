package de.paulm.four_in_a_row.web.exceptions.advice;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {

    @NonNull
    private final HttpStatus status;
    private final String errorCode;
    private final String field;
    private final Map<String, Object> additionalDetails;

    protected ApplicationException(String message, @NonNull HttpStatus status, String errorCode) {
        this(message, status, errorCode, null, Map.of());
    }

    protected ApplicationException(String message, @NonNull HttpStatus status, String errorCode, String field) {
        this(message, status, errorCode, field, Map.of());
    }

    // Der "Master"-Konstruktor
    protected ApplicationException(String message, @NonNull HttpStatus status, String errorCode, String field,
            Map<String, Object> details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.field = field;
        this.additionalDetails = details != null ? details : Map.of();
    }

}
