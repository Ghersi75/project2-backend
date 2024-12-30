package com.team2.backend.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.team2.backend.DTO.User.ChangeDisplayNameDTO;
import com.team2.backend.DTO.User.ChangePasswordDTO;
import com.team2.backend.DTO.User.ChangeUsernameDTO;
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

    @Test
    void createUser_ShouldCreateUserAndReturnToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken("testuser")).thenReturn("dummyToken");

        String encryptedPassword = "encryptedPassword123";
        when(passwordEncoder.encode("password")).thenReturn(encryptedPassword);

        String token = userService.createUser(user);

        assertEquals("dummyToken", token);
        assertEquals(encryptedPassword, user.getPassword()); 
        verify(userRepository, times(1)).save(user); 
        verify(passwordEncoder, times(1)).encode("password");

    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        User user = new User();
        user.setUsername("existinguser");
        user.setPassword("password");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() {
        User user = new User();
        user.setUsername("validuser");
        String rawPassword = "password";
        String hashedPassword = "hashedPassword123";
        user.setPassword(hashedPassword);

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("validuser")).thenReturn("dummyToken");
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        String token = userService.authenticateUser("validuser", "password");

        assertEquals("dummyToken", token);
        verify(userRepository, times(1)).findByUsername("validuser");
        verify(passwordEncoder, times(1)).matches(rawPassword, hashedPassword);
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUsernameIsInvalid() {
        when(userRepository.findByUsername("invaliduser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.authenticateUser("invaliduser", "password"));
        verify(userRepository, times(1)).findByUsername("invaliduser");
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenPasswordIsInvalid() {
        User user = new User();
        user.setUsername("validuser");
        String hashedPassword = "hashedPassword123";
        user.setPassword(hashedPassword);

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", hashedPassword)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.authenticateUser("validuser", "wrongpassword"));
        verify(userRepository, times(1)).findByUsername("validuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", hashedPassword);
                                                                                
    }

   
    @Test
    void testChangePassword_Success() {
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";
    
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode(oldPassword)); 
    
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true);  
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
    
        userService.changePassword(userId, new ChangePasswordDTO(oldPassword, newPassword, confirmPassword));
    
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(existingUser);  
        assertEquals("encodedNewPassword", existingUser.getPassword());
    }

    @Test
    void testChangePassword_OldPasswordIncorrect() {
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode("incorrectOldPassword"));  

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(false);  

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.changePassword(userId, new ChangePasswordDTO(oldPassword, newPassword, confirmPassword));
        });

        assertEquals("Old password is incorrect.", exception.getMessage());
    }

    @Test
    void testChangePassword_NewPasswordMismatch() {
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";
        String confirmPassword = "mismatchedPassword123";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode(oldPassword));  

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true); 

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            userService.changePassword(userId, new ChangePasswordDTO(oldPassword, newPassword, confirmPassword));
        });

        assertEquals("New password and confirmation do not match.", exception.getMessage()); 
    }

    @Test
    void testChangeDisplayName_Success() {
        Long userId = 1L;
        String newDisplayName = "New Display Name";
        String password = "Password123";
    
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setDisplayName("Old Display Name");
        existingUser.setPassword(passwordEncoder.encode(password));
    
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(password, existingUser.getPassword())).thenReturn(true);
    
        userService.changeDisplayName(userId, new ChangeDisplayNameDTO(newDisplayName, password));
    
        assertEquals(newDisplayName, existingUser.getDisplayName());
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    void testChangeDisplayName_UserNotFound() {
        Long userId = 1L;
        String newDisplayName = "New Display Name";
        String Password = "Password123";

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changeDisplayName(userId,new ChangeDisplayNameDTO(newDisplayName,Password));
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testChangeUsername_Success() {
        Long userId = 1L;
        String oldPassword = "Password123";
        String newUsername = "NewUsername";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode(oldPassword));
        existingUser.setUsername("OldUsername");

        ChangeUsernameDTO changeUsernameDTO = new ChangeUsernameDTO(newUsername, oldPassword);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true);

        userService.changeUsername(userId, changeUsernameDTO);

        assertEquals(newUsername, existingUser.getUsername());
        Mockito.verify(userRepository, Mockito.times(1)).save(existingUser);
    }

    @Test
    void testChangeUsername_UserNotFound() {
        Long userId = 1L;
        ChangeUsernameDTO changeUsernameDTO = new ChangeUsernameDTO("NewUsername", "Password123");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changeUsername(userId, changeUsernameDTO)
        );
        assertEquals("User not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testChangeUsername_PasswordMismatch() {
        Long userId = 1L;
        String wrongPassword = "WrongPassword";
        String newUsername = "NewUsername";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode("CorrectPassword"));
        existingUser.setUsername("OldUsername");

        ChangeUsernameDTO changeUsernameDTO = new ChangeUsernameDTO(newUsername, wrongPassword);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(wrongPassword, existingUser.getPassword())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changeUsername(userId, changeUsernameDTO)
        );
        assertEquals("Password is incorrect.", exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testChangeUsername_InvalidUsername() {
        Long userId = 1L;
        String oldPassword = "Password123";
        String invalidUsername = " ";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword(passwordEncoder.encode(oldPassword));
        existingUser.setUsername("OldUsername");

        ChangeUsernameDTO changeUsernameDTO = new ChangeUsernameDTO(invalidUsername, oldPassword);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changeUsername(userId, changeUsernameDTO)
        );
        assertEquals("Username name is required", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }
}
