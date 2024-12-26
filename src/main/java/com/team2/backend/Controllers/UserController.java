package com.team2.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team2.backend.dto.UserRequestDTO;
import com.team2.backend.dto.UserResponseDTO;
import com.team2.backend.entity.User;
import com.team2.backend.service.UserService;

import jakarta.validation.Valid;

/**
 * REST controller for managing user-related operations.
 * Handles HTTP requests for user registration and delegates
 * the business logic to the UserService layer.
 */
@RestController
public class UserController {

    // Injects the UserService instance for handling user-related business logic.
    @Autowired
    private UserService userService;

    /**
     * Endpoint to register a new user.
     * 
     * @param userRequestDTO The user details sent in the request body as a JSON object.
     * @return A ResponseEntity containing either the newly created user object
     *         with a 200 OK status, or an error message with a 400 Bad Request status.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            // Convert UserRequestDTO to User entity
            User user = new User();
            user.setUsername(userRequestDTO.getUsername());
            user.setPassword(userRequestDTO.getPassword());
            user.setRole("CONTRIBUTOR");
            

            // Call the UserService to register the user
            String token = userService.createUser(user);

            ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(true) // Only send over HTTPS
            .path("/")
            .maxAge(24 * 60 * 60) // 1 day
            .sameSite("Strict") // Protect against CSRF
            .build();


            return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie", cookie.toString()).body("Account Registered: Login successful.");

        } catch (Exception e) {
            // Handle exceptions by returning a 400 Bad Request status with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint to log in a user.
     *
     * @param loginRequestDTO The login credentials provided in the request body.
     * @return A ResponseEntity containing a success message or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserRequestDTO loginRequestDTO) {
        try {
            // Call the UserService to authenticate the user
            String token = userService.authenticateUser(
                loginRequestDTO.getUsername(), 
                loginRequestDTO.getPassword()
            );

            ResponseCookie cookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(true) // Only send over HTTPS
            .path("/")
            .maxAge(24 * 60 * 60) // 1 day
            .sameSite("Strict") // Protect against CSRF
            .build();


            return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie", cookie.toString()).body("Login successful.");
        } catch (Exception e) {
            // Handle failed authentication attempts
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
