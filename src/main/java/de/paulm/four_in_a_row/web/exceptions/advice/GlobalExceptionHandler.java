package de.paulm.four_in_a_row.web.exceptions.advice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import de.paulm.four_in_a_row.domain.exceptions.IllegalDisplayNameException;
import de.paulm.four_in_a_row.domain.exceptions.PlayerProfileNotFoundException;
import de.paulm.four_in_a_row.domain.exceptions.PlayerStatisticNotFoundException;
import de.paulm.four_in_a_row.domain.exceptions.UserSessionNotFoundException;
import de.paulm.four_in_a_row.web.exceptions.ForbiddenException;
import de.paulm.four_in_a_row.web.exceptions.RateLimitExceededException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ PlayerStatisticNotFoundException.class, PlayerProfileNotFoundException.class })
    public ResponseEntity<ApiError> handlePlayerNotFound(RuntimeException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                getDescriptionWithoutContextInfo(request),
                Map.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                getDescriptionWithoutContextInfo(request),
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
                getDescriptionWithoutContextInfo(request),
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
                getDescriptionWithoutContextInfo(request),
                details);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiError> handleRateLimit(RateLimitExceededException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.TOO_MANY_REQUESTS,
                getDescriptionWithoutContextInfo(request),
                Map.of("retry_after_seconds", ex.getRetryAfterSeconds()));
        String secondsStr = String.valueOf(ex.getRetryAfterSeconds());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", secondsStr)
                .body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                getDescriptionWithoutContextInfo(request),
                Map.of());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                getDescriptionWithoutContextInfo(request),
                Map.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(IllegalDisplayNameException.class)
    public ResponseEntity<ApiError> handleIllegalDisplayName(IllegalDisplayNameException ex, WebRequest request) {
        ApiError error = new ApiError(
                "Ungültiger Anzeigename",
                HttpStatus.BAD_REQUEST,
                getDescriptionWithoutContextInfo(request),
                Map.of("displayName", ex.getMessage()));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UserSessionNotFoundException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(UserSessionNotFoundException ex, WebRequest request) {
        ApiError error = new ApiError(
                "Ungültiges RefreshToken",
                HttpStatus.UNAUTHORIZED,
                getDescriptionWithoutContextInfo(request),
                Map.of());
        log.warn("RefreshToken ungültig: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        ApiError error = new ApiError(
                "Ein unerwarteter Fehler ist aufgetreten",
                HttpStatus.INTERNAL_SERVER_ERROR,
                getDescriptionWithoutContextInfo(request),
                Map.of());
        log.error("Unerwarteter Fehler: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String getDescriptionWithoutContextInfo(WebRequest request) {
        return request.getDescription(false).replace("uri=", ""); // Gibt den Pfad ohne Zusatzinfos
    }
}
