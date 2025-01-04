package com.team2.backend.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.team2.backend.exceptions.InvalidCredentialsException;
import com.team2.backend.controllers.GlobalExceptionHandler;

public class GlobalExceptionHandlerTest {
    @Test
    void testInvalidCredentialsExceptionHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid username.");
        ResponseEntity<Map<String, String>> response = handler.handleInvalidCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(Map.of("message", "Invalid username."), response.getBody());
    }

}
