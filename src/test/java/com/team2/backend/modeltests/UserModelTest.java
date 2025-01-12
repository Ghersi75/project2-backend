package com.team2.backend.modeltests;

import com.team2.backend.dto.user.UserSignUpDTO;
import com.team2.backend.enums.UserRole;
import org.junit.jupiter.api.Test;

import com.team2.backend.models.*;
import com.team2.backend.enums.UserRole.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructor_FromDTO() {
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setUsername("testuser");
        userSignUpDTO.setDisplayName("Test User");
        userSignUpDTO.setPassword("password123");
        userSignUpDTO.setRole("ADMIN");  

        User user = new User(userSignUpDTO);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("Test User", user.getDisplayName());
        assertEquals("password123", user.getPassword());
        assertEquals(UserRole.MODERATOR, user.getUserRole());  
    }

    @Test
    void testUserConstructor_DefaultRole() {
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setUsername("testuser");
        userSignUpDTO.setDisplayName("Test User");
        userSignUpDTO.setPassword("password123");

        User user = new User(userSignUpDTO);

        assertNotNull(user);
        assertEquals(UserRole.CONTRIBUTOR, user.getUserRole()); 
}
