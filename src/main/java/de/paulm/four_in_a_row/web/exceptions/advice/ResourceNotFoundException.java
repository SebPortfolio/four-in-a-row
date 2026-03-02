package de.paulm.four_in_a_row.web.exceptions.advice;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message, String errorCode, String field) {
        super(message, HttpStatus.NOT_FOUND, errorCode, field);
    }
}
