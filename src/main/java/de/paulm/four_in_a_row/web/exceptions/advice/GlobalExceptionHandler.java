package de.paulm.four_in_a_row.web.exceptions.advice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.domain.exceptions.PlayerStatisticNotFoundException;
import de.paulm.four_in_a_row.web.exceptions.RateLimitExceededException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ PlayerStatisticNotFoundException.class, PlayerProfileNotFoundException.class })
    public ResponseEntity<ApiError> handlePlayerNotFound(RuntimeException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getDescription(false).replace("uri=", ""), // Gibt den Pfad ohne Zusatzinfos
                Map.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getDescription(false).replace("uri=", ""),
                Map.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> errorMap = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        violation -> violation.getMessage(),
                        // Merge-Funktion falls zwei Constraints das gleiche Feld betreffen
                        (existing, replacement) -> existing + " & " + replacement));
        ApiError error = new ApiError(
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST,
                request.getDescription(false).replace("uri=", ""),
                errorMap);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err -> details.put(err.getField(), err.getDefaultMessage()));

        ApiError error = new ApiError(
                "Validierung fehlgeschlagen",
                HttpStatus.BAD_REQUEST,
                request.getDescription(false).replace("uri=", ""),
                details);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiError> handleRateLimit(RateLimitExceededException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.TOO_MANY_REQUESTS,
                request.getDescription(false).replace("uri=", ""),
                Map.of("retry_after", "Wait a few seconds"));
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).header("Retry-After", "60").body(error);
    }
}
