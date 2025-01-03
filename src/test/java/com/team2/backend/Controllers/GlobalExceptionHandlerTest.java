package com.team2.backend.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.team2.backend.Exceptions.Status401.InvalidCredentialsException;
import com.team2.backend.Exceptions.Status401.Status401Exception;

public class GlobalExceptionHandlerTest {
    @Test
    void testInvalidCredentialsExceptionHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Status401Exception ex = new Status401Exception("Invalid username.");
        Map<String, String> response = handler.Status401Handler(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response);
        assertEquals(Map.of("message", "Invalid username."), response);
    }

}
