package com.example.demo.exceptions;

import com.example.demo.exceptions.api.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReferenceNotFoundException.class)
    public ResponseEntity<ApiError> handleReferenceNotFound(
            ReferenceNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiError(
                        "REFERENCE_NOT_FOUND",
                        ex.getMessage(),
                        Map.of(
                                "resource", ex.getResource().name(),
                                "field", ex.getField()
                        )
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex
    ) {
        String message = ex.getMessage();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(
                        "BAD_CREDENTIALS",
                        message,
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(err ->
                        fieldErrors.put(err.getField(), err.getDefaultMessage())
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        "VALIDATION_ERROR",
                        "Validation failed for the following fields: "
                                + String.join(", ", fieldErrors.keySet()),
                        Map.of("fields", fieldErrors)
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            BadRequestException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        "BAD_REQUEST",
                        ex.getMessage(),
                        Map.of(
                                "resource", ex.getResource().name(),
                                "field", ex.getField(),
                                "reason", ex.getReason()
                        )
                ));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExists(
            AlreadyExistsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        "ALREADY_EXISTS",
                        ex.getMessage(),
                        Map.of(
                                "resource", ex.getResource().name(),
                                "field", ex.getField()
                        )
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(
                        "INTERNAL_ERROR",
                        "Unexpected server error",
                        null
                ));
    }

}
