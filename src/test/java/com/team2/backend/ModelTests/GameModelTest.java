package com.team2.backend.ModelTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.team2.backend.DTO.Game.*;
import com.team2.backend.Models.*;

public class GameModelTest {

    @Test
    void testGameInitialization() {
        // Arrange
        String steamid = "123456";
        String title = "Test Game";
        String description = "This is a test description.";

        // Act
        Game game = new Game();
        game.setSteamid(steamid);
        game.setTitle(title);
        game.setDescription(description);

        // Assert
        assertNotNull(game);
        assertEquals(steamid, game.getSteamid());
        assertEquals(title, game.getTitle());
        assertEquals(description, game.getDescription());
    }

    @Test
    void testAddReviewToGame() {
        // Arrange
        Game game = new Game();
        Review review1 = new Review();
        review1.setContent("Great game!");
        Review review2 = new Review();
        review2.setContent("Not bad.");

        List<Review> reviews = new ArrayList<>();
        reviews.add(review1);
        reviews.add(review2);

        // Act
        game.setReviews(reviews);

        // Assert
        assertNotNull(game.getReviews());
        assertEquals(2, game.getReviews().size());
        assertEquals("Great game!", game.getReviews().get(0).getContent());
        assertEquals("Not bad.", game.getReviews().get(1).getContent());
    }

    @Test
    void testGameDTOConstructor() {
        // Arrange
        String steamid = "123456";
        String title = "Test Game";
        String description = "This is a test description.";
        GameDTO gameDTO = new GameDTO(steamid, title, description);

        // Act
        Game game = new Game(gameDTO);

        // Assert
        assertNotNull(game);
        assertEquals(gameDTO.getSteamid(), game.getSteamid());
        assertEquals(gameDTO.getTitle(), game.getTitle());
        assertEquals(gameDTO.getDescription(), game.getDescription());
    }

    @Test
    void testSetAndGetFields() {
        // Arrange
        Game game = new Game();
        game.setId(1L);
        game.setSteamid("123456");
        game.setTitle("Test Title");
        game.setDescription("Test Description");

        // Assert
        assertEquals(1L, game.getId());
        assertEquals("123456", game.getSteamid());
        assertEquals("Test Title", game.getTitle());
        assertEquals("Test Description", game.getDescription());
    }
}