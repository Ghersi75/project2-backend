package com.team2.backend.Controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.team2.backend.Exceptions.Status400.Status400Exception;
import com.team2.backend.Exceptions.Status401.Status401Exception;
import com.team2.backend.Exceptions.Status409.Status409Exception;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Status 400 - Bad Request
    // Thrown if user sends invalid data
    // Example: Invalid userid, gameid, enumvalue
    @ExceptionHandler(Status400Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> Status400Handler(Status400Exception e) {
        return Map.of("error", e.getMessage());
    }

    // Status 401 - Unauthorized
    // Thrown if user login is unsuccessful or user doesn't have access to an
    // endpoint
    // Example: EMPLOYEE Role user tries to access MANAGER only endpoints
    @ExceptionHandler(Status401Exception.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> Status401Handler(Status401Exception e) {
        return Map.of("error", e.getMessage());
    }

    // Status 409 - Conflict
    // Thrown if a unique user field is already taken
    // Example: User SignUp username or email already taken
    @ExceptionHandler(Status409Exception.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> Status409Handler(Status409Exception e) {
        return Map.of("error", e.getMessage());
    }

    // @ExceptionHandler(UserAlreadyExistsException.class)
    // public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    //     return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    // }

    // @ExceptionHandler(InvalidCredentialsException.class)
    // public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
    //     Map<String, String> response = new HashMap<>();
    //     response.put("message", ex.getMessage());
    //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    // }

    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    //     Map<String, String> errors = new HashMap<>();
    //     ex.getBindingResult().getAllErrors().forEach(error -> {
    //         String fieldName = ((FieldError) error).getField();
    //         String errorMessage = error.getDefaultMessage();
    //         errors.put(fieldName, errorMessage);
    //     });
    //     return errors;
    // }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<String> handleGeneralException(Exception ex) {
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body("An unexpected error occurred: " + ex.getMessage());
    // }

}
