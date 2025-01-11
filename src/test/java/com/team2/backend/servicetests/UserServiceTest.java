package com.team2.backend.servicetests;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.team2.backend.dto.user.ChangeDisplayNameDTO;
import com.team2.backend.dto.user.ChangePasswordDTO;
import com.team2.backend.dto.user.ChangeUsernameDTO;
import com.team2.backend.enums.UserRole;
import com.team2.backend.exceptions.InvalidCredentialsException;
import com.team2.backend.exceptions.UserAlreadyExistsException;
import com.team2.backend.models.User;
import com.team2.backend.repository.UserRepository;
import com.team2.backend.service.UserService;
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
        user.setUserRole(UserRole.CONTRIBUTOR);
        user.setDisplayName("Display NAme");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(jwtUtil.generateToken("testuser", UserRole.CONTRIBUTOR, "Display NAme")).thenReturn("dummyToken");

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
        user.setUserRole(UserRole.CONTRIBUTOR);
        user.setDisplayName("Display NAme");

        when(userRepository.findByUsername("validuser")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("validuser", UserRole.CONTRIBUTOR, "Display NAme")).thenReturn("dummyToken");
        // Mock password verification (use BCrypt to simulate password matching)
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
    void testChangeUsername_Success() {
        String currentUsername = "oldUser";
        String newUsername = "newUser";
        ChangeUsernameDTO dto = new ChangeUsernameDTO(newUsername);
        User user = new User();
        user.setUsername(currentUsername);

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(newUsername, user.getUserRole(), user.getDisplayName())).thenReturn("newToken");

        String token = userService.changeUsername(currentUsername, dto);

        assertEquals("newToken", token);
        assertEquals(newUsername, user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void testChangeUsername_UserNotFound() {
        String currentUsername = "oldUser";
        ChangeUsernameDTO dto = new ChangeUsernameDTO("newUser");

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> {
            userService.changeUsername(currentUsername, dto);
        });
    }

    @Test
    void testChangeUsername_UsernameAlreadyTaken() {
        String currentUsername = "oldUser";
        String newUsername = "newUser";
        ChangeUsernameDTO dto = new ChangeUsernameDTO(newUsername);
        User user = new User();
        user.setUsername(currentUsername);

        when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(newUsername)).thenReturn(Optional.of(new User()));

        assertThrows(InvalidCredentialsException.class, () -> {
            userService.changeUsername(currentUsername, dto);
        });
    }

    @Test
void testChangePassword_Success() {
    String username = "testUser";
    String oldPassword = "oldPass";
    String newPassword = "newPass";
    ChangePasswordDTO dto = new ChangePasswordDTO(oldPassword, newPassword);
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(oldPassword));

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
    when(jwtUtil.generateToken(username, user.getUserRole(), user.getDisplayName())).thenReturn("newToken");

    String token = userService.changePassword(username, dto);

    assertEquals("newToken", token);
    verify(userRepository).save(user);
}

@Test
void testChangePassword_InvalidOldPassword() {
    String username = "testUser";
    ChangePasswordDTO dto = new ChangePasswordDTO("wrongOldPass", "newPass");
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode("oldPass"));

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrongOldPass", user.getPassword())).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> {
        userService.changePassword(username, dto);
    });
}

@Test
void testChangePassword_SameAsOldPassword() {
    String username = "testUser";
    String password = "samePass";
    ChangePasswordDTO dto = new ChangePasswordDTO(password, password);
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

    assertThrows(InvalidCredentialsException.class, () -> {
        userService.changePassword(username, dto);
    });
}

@Test
void testChangeDisplayName_Success() {
    String username = "testUser";
    String newDisplayName = "New Display Name";
    ChangeDisplayNameDTO dto = new ChangeDisplayNameDTO(newDisplayName);
    User user = new User();
    user.setUsername(username);
    user.setDisplayName("Old Display Name");

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    when(jwtUtil.generateToken(username, user.getUserRole(), newDisplayName)).thenReturn("newToken");

    String token = userService.changeDisplayName(username, dto);

    assertEquals("newToken", token);
    assertEquals(newDisplayName, user.getDisplayName());
    verify(userRepository).save(user);
}

@Test
void testChangeDisplayName_DisplayNameRequired() {
    String username = "testUser";
    ChangeDisplayNameDTO dto = new ChangeDisplayNameDTO("   ");
    User user = new User();
    user.setUsername(username);

    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

    assertThrows(InvalidCredentialsException.class, () -> {
        userService.changeDisplayName(username, dto);
    });
}
}
