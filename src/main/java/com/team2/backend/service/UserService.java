package com.team2.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.team2.backend.exceptions.InvalidCredentialsException;
import com.team2.backend.exceptions.UserAlreadyExistsException;
import com.team2.backend.utils.JwtUtil;
import com.team2.backend.dto.User.ChangeDisplayNameDTO;
import com.team2.backend.dto.User.ChangePasswordDTO;
import com.team2.backend.dto.User.ChangeUsernameDTO;
import com.team2.backend.models.User;
import com.team2.backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user.
     *
     * @param user The user object containing the user details.
     * @return The newly created user object.
     * @throws Exception if the email or username is already in use.
     */
    public String createUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new InvalidCredentialsException("Username cannot be empty.");
        }
        if (!userRepository.findByUsername(user.getUsername()).isEmpty()) {
            throw new UserAlreadyExistsException("Username is already in use.");
        }
        // Encrypt the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save to database
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserRole(), user.getDisplayName());
        return token;
    }

    /**
     * Authenticates a user based on their username and password.
     *
     * @param usernameOrEmail The username provided by the user.
     * @param password        The password provided by the user.
     * @return A token or success message if authentication is successful.
     * @throws Exception if authentication fails.
     */
    public String authenticateUser(String username, String password) {
        // Find the user by username or email
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username.");
        }
        User user = optionalUser.get();
        // Verify the password (ensure you store hashed passwords in the database)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        // If using JWT, generate a token here (optional)
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserRole(), user.getDisplayName());
        return token;
    }

    public void changeUsername(Long userId, ChangeUsernameDTO changeUsernameDTO) {
        String newUsername = changeUsernameDTO.getNewUsername();
        String password = changeUsernameDTO.getPassword();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Password is incorrect.");
        }
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new InvalidCredentialsException("Username name is required");
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect.");
        }
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new InvalidCredentialsException("New password cannot be the same as the old password.");
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirmation do not match.");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));

        userRepository.save(user);

    }

    public void changeDisplayName(Long userId, ChangeDisplayNameDTO changeDisplayNameDTO) {
        String newDisplayName = changeDisplayNameDTO.getNewDisplayName();
        String password = changeDisplayNameDTO.getPassword();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Password is incorrect.");
        }
        if (newDisplayName == null || newDisplayName.trim().isEmpty()) {
            throw new InvalidCredentialsException("Display name is required");
        }
        user.setDisplayName(newDisplayName);
        userRepository.save(user);
    }

}
