package com.team2.backend.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.team2.backend.Exceptions.InvalidCredentialsException;
import com.team2.backend.Exceptions.UserAlreadyExistsException;
import com.team2.backend.Models.User;
import com.team2.backend.Repository.UserRepository;
import com.team2.backend.util.JwtUtil;

public class UserServiceTest {
     @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldCreateUserAndReturnToken() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken("testuser")).thenReturn("dummyToken");

        // Act
        String token = userService.createUser(user);

        // Assert
        assertEquals("dummyToken", token);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        User user = new User();
        user.setUsername("validuser");
        user.setPassword("password");

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("validuser")).thenReturn("dummyToken");

        // Act
        String token = userService.authenticateUser("validuser", "password");

        // Assert
        assertEquals("dummyToken", token);
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUsernameIsInvalid() {
        // Arrange
        when(userRepository.findByUsername("invaliduser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userService.authenticateUser("invaliduser", "password"));
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        User user = new User();
        user.setUsername("validuser");
        user.setPassword("correctpassword");

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userService.authenticateUser("validuser", "wrongpassword"));
    }
    
}
