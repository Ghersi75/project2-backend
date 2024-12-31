package com.team2.backend.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.checkerframework.checker.units.qual.s;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.team2.backend.DTO.User.ChangePasswordDTO;
import com.team2.backend.Enums.UserRole;
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

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // CreateUser Tests
    @Test
    void createUser_ShouldCreateUserAndReturnToken() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setUserRole(UserRole.CONTRIBUTOR);
        user.setDisplayName("Display NAme");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken("testuser",UserRole.CONTRIBUTOR,"Display NAme")).thenReturn("dummyToken");

        String encryptedPassword = "encryptedPassword123";
        when(passwordEncoder.encode("password")).thenReturn(encryptedPassword);

        // Act
        String token = userService.createUser(user);

        assertEquals("dummyToken", token);
        assertEquals(encryptedPassword, user.getPassword()); // Verify the password is encrypted
        verify(userRepository, times(1)).save(user); // Verify the save method was called once
        verify(passwordEncoder, times(1)).encode("password"); // Verify the encoding method was called

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

    // AuthenticateUser Tests
    @Test
    void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        User user = new User();
        user.setUsername("validuser");
        String rawPassword = "password";
        String hashedPassword = "hashedPassword123";
        user.setPassword(hashedPassword);
        user.setUserRole(UserRole.CONTRIBUTOR);
        user.setDisplayName("Display NAme");

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("validuser",UserRole.CONTRIBUTOR,"Display NAme")).thenReturn("dummyToken");
        // Mock password verification (use BCrypt to simulate password matching)
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Act
        String token = userService.authenticateUser("validuser", "password");

        // Assert
        assertEquals("dummyToken", token);
        verify(userRepository, times(1)).findByUsername("validuser"); // Ensure repository lookup
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword); // Ensure password matching is checked
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUsernameIsInvalid() {
        // Arrange
        when(userRepository.findByUsername("invaliduser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> userService.authenticateUser("invaliduser", "password"));
        verify(userRepository, times(1)).findByUsername("invaliduser");
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenPasswordIsInvalid() {
        // Arrange
        User user = new User();
        user.setUsername("validuser");
        String correctPassword = "correctpassword";
        String hashedPassword = "hashedPassword123";
        user.setPassword(hashedPassword);

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", hashedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.authenticateUser("validuser", "wrongpassword"));
        verify(userRepository, times(1)).findByUsername("validuser"); // Ensure repository lookup
        verify(passwordEncoder, times(1)).matches("wrongpassword", hashedPassword); // Ensure password matching is
                                                                                    // checked
    }

   

    // ChangePassword Tests
    @Test
    void testChangePassword_Success() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode(oldPassword));  // Set encoded password
        System.out.println(existingUser.getPassword());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true);  // Mock password match
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword"); 

        when(passwordEncoder.matches(newPassword, "encodedNewPassword")).thenReturn(true);

        // Act
        userService.changePassword(userId, new ChangePasswordDTO(oldPassword, newPassword, confirmPassword));

        System.out.println(existingUser.getPassword());
        // Assert
        verify(passwordEncoder, times(1)).encode(newPassword); // Ensure encoding method was called
        assertTrue(passwordEncoder.matches(newPassword, existingUser.getPassword()));  // Verify the password was updated
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testChangePassword_OldPasswordIncorrect() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode("incorrectOldPassword"));  // Set an incorrect encoded password

        // Mock the repository method to return the existing user
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(false);  // Mock password mismatch

        // Act and Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.changePassword(userId, new ChangePasswordDTO(oldPassword, newPassword, confirmPassword));
        });

        assertEquals("Old password is incorrect.", exception.getMessage());  // Verify exception message
    }

    @Test
    void testChangePassword_NewPasswordMismatch() {
        // Arrange
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String confirmPassword = "mismatchedPassword123";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode(oldPassword));  // Set encoded password

        // Mock the repository method to return the existing user
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true);  // Mock password match

        // Act and Assert
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.changePassword(userId, new ChangePasswordDTO(oldPassword, newPassword, confirmPassword));
        });

        assertEquals("New password and confirmation do not match.", exception.getMessage());  // Verify exception message
    }


    // ChangeDisplayName Tests
    @Test
    void testChangeDisplayName_Success() {
        // Arrange
        Long userId = 1L;
        String newDisplayName = "New Display Name";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDisplayName("Old Display Name");

        // Mock the repository method to return the existing user
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        userService.changeDisplayName(userId, newDisplayName);
        System.out.println(existingUser.getDisplayName());

        // Assert
        assertEquals(newDisplayName, existingUser.getDisplayName()); // Verify display name change
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser); // Verify save method was called once
    }

    @Test
    void testChangeDisplayName_UserNotFound() {
        // Arrange
        Long userId = 1L;
        String newDisplayName = "New Display Name";

        // Mock repository to return an empty Optional (user not found)
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changeDisplayName(userId, newDisplayName);
        });

        assertEquals("User not found", exception.getMessage()); // Verify exception message
    }

}
