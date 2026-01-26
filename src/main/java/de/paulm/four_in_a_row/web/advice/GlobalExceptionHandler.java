package de.paulm.four_in_a_row.web.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.paulm.four_in_a_row.domain.exceptions.SpielerStatistikNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(SpielerStatistikNotFoundException.class)
    public ResponseEntity<ApiError> handleSpielerStatistikNotFoundException(SpielerStatistikNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }
}
