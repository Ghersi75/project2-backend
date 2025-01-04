package com.team2.backend.ModelTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.team2.backend.enums.UserRole;
import com.team2.backend.dto.User.UserSignUpDTO;
import com.team2.backend.models.*;

import java.util.List;

public class UserModelTest {
    @Test
    void testUserInitialization() {
        String displayName = "John Doe";
        String username = "johndoe";
        String password = "password123";
        UserRole userRole = UserRole.CONTRIBUTOR;

        User user = new User();
        user.setDisplayName(displayName);
        user.setUsername(username);
        user.setPassword(password);
        user.setUserRole(userRole);

        assertNotNull(user);
        assertEquals(displayName, user.getDisplayName());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(userRole, user.getUserRole());
    }

    @Test
    void testUserSignUpDTOConstructor() {
        UserSignUpDTO dto = new UserSignUpDTO();
        dto.setUsername("johndoe");
        dto.setDisplayName("John Doe");
        dto.setPassword("password123");
        dto.setRole("MODERATOR");

        User user = new User(dto);

        assertNotNull(user);
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getDisplayName(), user.getDisplayName());
        assertEquals(dto.getPassword(), user.getPassword());
        assertEquals(UserRole.MODERATOR, user.getUserRole());
    }

    @Test
    void testUserRoleDefaultsToContributor() {
        UserSignUpDTO dto = new UserSignUpDTO();
        dto.setUsername("johndoe");
        dto.setDisplayName("John Doe");
        dto.setPassword("password123");
        dto.setRole(null);

        User user = new User(dto);

        assertEquals(UserRole.CONTRIBUTOR, user.getUserRole());
    }

    @Test
    void testAddFavoriteGames() {
        User user = new User();
        Game game1 = new Game();
        Game game2 = new Game();

        List<Game> favoriteGames = new ArrayList<>();
        favoriteGames.add(game1);
        favoriteGames.add(game2);

        user.setFavoriteGames(favoriteGames);

        assertNotNull(user.getFavoriteGames());
        assertEquals(2, user.getFavoriteGames().size());
        assertTrue(user.getFavoriteGames().contains(game1));
        assertTrue(user.getFavoriteGames().contains(game2));
    }

    @Test
    void testAddReviews() {
        User user = new User();
        Review review1 = new Review();
        Review review2 = new Review();

        List<Review> reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);

        user.setReviews(reviews);

        assertNotNull(user.getReviews());
        assertEquals(2, user.getReviews().size());
        assertTrue(user.getReviews().contains(review1));
        assertTrue(user.getReviews().contains(review2));
    }

    @Test
    void testAddReviewInteractions() {
        User user = new User();
        UserReviewInteraction interaction1 = new UserReviewInteraction();
        UserReviewInteraction interaction2 = new UserReviewInteraction();

        List<UserReviewInteraction> interactions = new ArrayList<>();
        interactions.add(interaction1);
        interactions.add(interaction2);

        user.setReviewInteractions(interactions);

        assertNotNull(user.getReviewInteractions());
        assertEquals(2, user.getReviewInteractions().size());
        assertTrue(user.getReviewInteractions().contains(interaction1));
        assertTrue(user.getReviewInteractions().contains(interaction2));
    }
}
