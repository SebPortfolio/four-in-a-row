package de.paulm.four_in_a_row.web.exceptions.advice;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ApiError(
        String message,
        int httpStatus,
        String httpError,
        String errorCode, // technischer Key (z.B. "USER_NOT_FOUND")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp,
        String path,
        Map<String, Object> details) {

    public ApiError(String message, HttpStatus httpStatus, String errorCode, String path,
            Map<String, Object> paramMap) {
        this(message, httpStatus.value(), httpStatus.getReasonPhrase(), errorCode, LocalDateTime.now(), path, paramMap);
    }
}
