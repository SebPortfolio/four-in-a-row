package de.paulm.four_in_a_row.web.exceptions.advice;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ApiError(
        String message,
        int statusCode,
        String error,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp,
        String path,
        Map<String, Object> details) {

    public ApiError(String message, HttpStatus status, String path, Map<String, Object> paramMap) {
        this(message, status.value(), status.getReasonPhrase(), LocalDateTime.now(), path, paramMap);
    }
}
