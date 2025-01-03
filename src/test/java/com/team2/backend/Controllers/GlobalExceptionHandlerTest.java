package com.team2.backend.Controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.team2.backend.Exceptions.InvalidCredentialsException;
import com.team2.backend.Exceptions.Status401Exception;

import com.team2.backend.Controllers.*;

public class GlobalExceptionHandlerTest {
    // @Test
    // void testInvalidCredentialsExceptionHandler() {
    //     GlobalExceptionHandler handler = new GlobalExceptionHandler();

    //     GlobalExceptionHandler ex = new GlobalExceptionHandler("Invalid username.");
    //     Map<String, String> response = handler.GlobalExceptionHandler(ex);

    //     assertEquals(HttpStatus.UNAUTHORIZED, response);
    //     assertEquals(Map.of("message", "Invalid username."), response);
    // }

}
