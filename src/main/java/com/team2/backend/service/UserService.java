package com.team2.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.backend.entity.User;

import repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registers a new user.
     *
     * @param user The user object containing the user details.
     * @return The newly created user object.
     * @throws Exception if the email or username is already in use.
     */
    public String createUser(User user)  throws Exception{
     
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username is already in use.");
        }

        // Encrypt the password (ensure you store hashed passwords in the database)
        user.setPassword(user.getPassword());

        // Save to database
        userRepository.save(user);

        String token = "mock-token"; // Replace with actual token generation logic.

        return token;
    }

     /**
     * Authenticates a user based on their username and password.
     *
     * @param usernameOrEmail The username  provided by the user.
     * @param password The password provided by the user.
     * @return A token or success message if authentication is successful.
     * @throws Exception if authentication fails.
     */
    public String authenticateUser(String username, String password) throws Exception {
        // Find the user by username or email
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new Exception("Invalid username or email.");
        }

        User user = optionalUser.get();

        // Verify the password (ensure you store hashed passwords in the database)
        if (!user.getPassword().equals(password)) {
            throw new Exception("Invalid password.");
        }

        // If using JWT, generate a token here (optional)
        String token = "mock-token"; // Replace with actual token generation logic.

        return token;
    }
    
}
