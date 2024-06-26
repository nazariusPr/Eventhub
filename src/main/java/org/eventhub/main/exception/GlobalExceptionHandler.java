package org.eventhub.main.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNullEntityReferenceException(NullEntityReferenceException ex) {
        log.error("Null entity reference exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNullDtoReferenceException(NullDtoReferenceException ex) {
        log.error("DTO not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNotValidDateException(NotValidDateException ex) {
        log.error("Entity not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Entity not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleAccessIsDeniedException(AccessIsDeniedException ex) {
        log.error("Access is denied exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> jwtExpiredException(ExpiredJwtException ex) {
        log.error("JWT expired exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handlePasswordException(PasswordException ex) {
        log.error("Bad request with user password: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        log.error("Null pointer exception" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        if (errorMessage.contains("duplicate")) {
            if (errorMessage.contains("email")) {
                errorMessage = "Email is already taken";
            }
            else if (errorMessage.contains("username")) {
                errorMessage = "Username is already taken";
            }
            else if (errorMessage.contains("title")) {
                errorMessage = "Title is already taken";
            }
        }
        else {
            errorMessage = "Data violation error occurred";
        }

        log.error(ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
