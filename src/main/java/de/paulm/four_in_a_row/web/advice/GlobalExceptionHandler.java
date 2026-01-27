package de.paulm.four_in_a_row.web.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.paulm.four_in_a_row.domain.exceptions.PlayerStatisticNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerStatisticNotFoundException.class)
    public ResponseEntity<ApiError> handlePlayerStatisticNotFoundException(PlayerStatisticNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }
}
