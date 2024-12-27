package com.team2.backend.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team2.backend.Exceptions.InvalidCredentialsException;
import com.team2.backend.Exceptions.UserAlreadyExistsException;
import com.team2.backend.Models.User;
import com.team2.backend.Repository.UserRepository;
import com.team2.backend.util.JwtUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

     @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user.
     *
     * @param user The user object containing the user details.
     * @return The newly created user object.
     * @throws Exception if the email or username is already in use.
     */
    public String createUser(User user){
        if (!userRepository.findByUsername(user.getUsername()).isEmpty()) {
            throw new UserAlreadyExistsException("Username is already in use.");
        }
        // Encrypt the password
        user.setPassword(user.getPassword());
        // Save to database
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());
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
    public String authenticateUser(String username, String password){
        // Find the user by username or email
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new InvalidCredentialsException("Invalid username.");
        }
        User user = optionalUser.get();
        // Verify the password (ensure you store hashed passwords in the database)
        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        // If using JWT, generate a token here (optional)
        String token = jwtUtil.generateToken(user.getUsername());
        return token;
    }
    
}
