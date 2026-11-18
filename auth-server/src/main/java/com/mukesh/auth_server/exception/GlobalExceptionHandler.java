package com.mukesh.auth_server.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> invalidTokenException(InvalidTokenException invalidTokenException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), invalidTokenException.getMessage(), LocalDateTime.now()));
    }

    // The exists-checks give a clean 409; DataIntegrityViolationException covers concurrent duplicate requests.
    @ExceptionHandler({UserRecordAlreadyExistsException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateRecord(Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), "A record for this user already exists.", LocalDateTime.now()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), LocalDateTime.now()));
    }
}
