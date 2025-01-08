package com.team2.backend.modeltests;

import com.team2.backend.dto.user.UserSignUpDTO;
import com.team2.backend.enums.UserRole;
import com.team2.backend.models.Review;
import com.team2.backend.models.User;
import com.team2.backend.repository.ReviewRepository;
import com.team2.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserModelTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private User user;

    @BeforeEach
    public void setup() {
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setUsername("testuser");
        userSignUpDTO.setDisplayName("Test User");
        userSignUpDTO.setPassword("password");
        userSignUpDTO.setRole("CONTRIBUTOR");
        user = new User(userSignUpDTO);
    }

    @Test
    public void testSaveUser() {
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
    }

    @Test
    public void testFindUserById() {
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Test User", foundUser.get().getDisplayName());
    }

@Test
public void testCascadeDeleteReviews() {
    Review review1 = new Review();
    review1.setContent("First review content");
    review1.setUser(user); 

    Review review2 = new Review();
    review2.setContent("Second review content");
    review2.setUser(user); 

    user.setReviews(List.of(review1, review2));
    userRepository.save(user);

    assertEquals(1, userRepository.count());
    assertEquals(2, reviewRepository.count());

    userRepository.delete(user);

    assertEquals(0, userRepository.count());
    assertEquals(0, reviewRepository.count());
}

    @Test
    public void testUserConstructorWithDTO() {
        assertEquals("testuser", user.getUsername());
        assertEquals(UserRole.CONTRIBUTOR, user.getUserRole());
    }
}