package com.raovat.api.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("Message", exception.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistException(EmailAlreadyExistException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("Message", exception.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(DuplicateResourceException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("Message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("Message", exception.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(Exception ex) {

        RestError re = new RestError(HttpStatus.UNAUTHORIZED.toString(),
                "Authentication failed at controller advice");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<RestError> handleNullPointerException(Exception ex) {
        RestError re = new RestError(HttpStatus.BAD_REQUEST.toString(), "Missing some field");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(re);
    }
}
