package de.paulm.four_in_a_row.web.exceptions.advice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleApplicationException(ApplicationException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>(ex.getAdditionalDetails());
        if (ex.getField() != null) {
            details.put(ex.getField(), Map.of(
                    "code", ex.getErrorCode(),
                    "message", ex.getMessage()));
        }
        ApiError error = new ApiError(
                ex.getMessage(),
                ex.getStatus(),
                ex.getErrorCode(),
                getDescriptionWithoutContextInfo(request),
                details);
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
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
                "CONSTRAINT_VALIDATION",
                getDescriptionWithoutContextInfo(request),
                errorMap);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> errorInfo = new HashMap<>();
            errorInfo.put("code", fieldError.getCode());
            errorInfo.put("message", fieldError.getDefaultMessage());
            details.put(fieldError.getField(), errorInfo);
        }

        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            Map<String, String> errorInfo = new HashMap<>();

            // Extrahiere den Key aus dem MessageTemplate (ohne die {})
            String code = objectError.unwrap(jakarta.validation.metadata.ConstraintDescriptor.class)
                    .getMessageTemplate()
                    .replaceAll("[{}]", "");

            errorInfo.put("code", code); // Ergibt bspw. "validation.invalid_date_range"
            errorInfo.put("message", objectError.getDefaultMessage());

            // Da es kein einzelnes Feld ist, wird es unter einen generischen Key
            // oder den Objektnamen gesetzt (z.B. "ban")
            details.put(objectError.getObjectName(), errorInfo);
        }

        ApiError error = new ApiError(
                "Validierung fehlgeschlagen",
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                getDescriptionWithoutContextInfo(request),
                details);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex, WebRequest request) {
        ApiError error = new ApiError(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                "USERNAME_NOT_FOUND",
                getDescriptionWithoutContextInfo(request),
                Map.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        ApiError error = new ApiError(
                "Ein unerwarteter Fehler ist aufgetreten",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                getDescriptionWithoutContextInfo(request),
                Map.of());
        log.error("Unerwarteter Fehler: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String getDescriptionWithoutContextInfo(WebRequest request) {
        return request.getDescription(false).replace("uri=", ""); // Gibt den Pfad ohne Zusatzinfos
    }
}
