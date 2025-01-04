package com.team2.backend.ModelTests;

import com.team2.backend.Enums.ReviewInteraction;
import com.team2.backend.Exceptions.InvalidEnumValueException;
import com.team2.backend.Models.Game;
import com.team2.backend.Models.Review;
import com.team2.backend.Models.User;
import com.team2.backend.Models.UserReviewInteraction;
import com.team2.backend.DTO.Review.NewReviewDTO;
import com.team2.backend.DTO.UserReviewInteraction.UserReviewInteractionDTO;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewModelTest {

    @Test
    void testReviewInitialization() {
        User user = new User();
        user.setUsername("testUser");

        Game game = new Game();
        game.setTitle("Test Game");
        game.setDescription("A game for testing.");

        String content = "Great review!";
        int likes = 10;
        int dislikes = 2;

        Review review = new Review();
        review.setUser(user);
        review.setGame(game);
        review.setContent(content);
        review.setLikes(likes);
        review.setDislikes(dislikes);

        assertNotNull(review);
        assertEquals(user, review.getUser());
        assertEquals(game, review.getGame());
        assertEquals(content, review.getContent());
        assertEquals(likes, review.getLikes());
        assertEquals(dislikes, review.getDislikes());
    }

    @Test
    void testReviewDefaultValues() {
        Review review = new Review();
        
        int defaultLikes = review.getLikes();
        int defaultDislikes = review.getDislikes();

        assertEquals(0, defaultLikes); 
        assertEquals(0, defaultDislikes); 
    }

    @Test
    void testAddUserReviewInteraction() {
        User user = new User();
        user.setUsername("testUser");

        Game game = new Game();
        game.setTitle("Test Game");

        NewReviewDTO newReviewDTO = new NewReviewDTO("Great review!",game);
        Review review = new Review(user, newReviewDTO);

        UserReviewInteraction interaction = new UserReviewInteraction();
        interaction.setReview(review);
        interaction.setUser(user);
        interaction.setInteraction(ReviewInteraction.LIKE); // Set interaction to LIKE

        List<UserReviewInteraction> interactions = new ArrayList<>();
        interactions.add(interaction);

        review.setUserInteractions(interactions);

        assertNotNull(review.getUserInteractions());
        assertEquals(1, review.getUserInteractions().size());
        assertEquals(ReviewInteraction.LIKE, review.getUserInteractions().get(0).getInteraction());
        assertEquals(user, review.getUserInteractions().get(0).getUser());
    }

    @Test
    void testReviewConstructorWithArguments() {
        User user = new User();
        user.setUsername("testUser");

        Game game = new Game();
        game.setTitle("Test Game");

        String content = "Amazing game!";
        int likes = 0;
        int dislikes = 0;

        NewReviewDTO newReviewDTO = new NewReviewDTO(content,game);
        Review review = new Review(user, newReviewDTO);

        assertNotNull(review);
        assertEquals(user, review.getUser());
        assertEquals(game, review.getGame());
        assertEquals(content, review.getContent());
        assertEquals(likes, review.getLikes());
        assertEquals(dislikes, review.getDislikes());
    }

    @Test
    void testSetAndGetFields() {
        User user = new User();
        user.setUsername("testUser");

        Game game = new Game();
        game.setTitle("Test Game");

        Review review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setGame(game);
        review.setContent("Test review content");
        review.setLikes(5);
        review.setDislikes(0);

        assertEquals(1L, review.getId());
        assertEquals(user, review.getUser());
        assertEquals(game, review.getGame());
        assertEquals("Test review content", review.getContent());
        assertEquals(5, review.getLikes());
        assertEquals(0, review.getDislikes());
    }

    @Test
    void testReviewInteractionEnum() {
        User user = new User();
        user.setUsername("testUser");

        Game game = new Game();
        game.setTitle("Test Game");

        NewReviewDTO newReviewDTO = new NewReviewDTO("Great review!",game);
        Review review = new Review(user, newReviewDTO);

        UserReviewInteraction interactionLike = new UserReviewInteraction();
        interactionLike.setReview(review);
        interactionLike.setUser(user);
        interactionLike.setInteraction(ReviewInteraction.LIKE);

        UserReviewInteraction interactionDislike = new UserReviewInteraction();
        interactionDislike.setReview(review);
        interactionDislike.setUser(user);
        interactionDislike.setInteraction(ReviewInteraction.DISLIKE);

        assertEquals(ReviewInteraction.LIKE, interactionLike.getInteraction());
        assertEquals(ReviewInteraction.DISLIKE, interactionDislike.getInteraction());
    }

    @Test
    void testInvalidReviewInteractionEnumValue() {
        InvalidEnumValueException exception = assertThrows(InvalidEnumValueException.class, () -> {
            UserReviewInteraction interactionInvalid = new UserReviewInteraction();
            interactionInvalid.setInteraction(ReviewInteraction.fromString("INVALID"));
        });

        assertEquals("Invalid review. Value must be LIKE or DISLIKE", exception.getMessage());
    }

    @Test
    void testUserReviewInteractionWithDTO() {
        User user = new User();
        user.setUsername("testUser");

        Game game = new Game();
        game.setTitle("Test Game");

        NewReviewDTO newReviewDTO = new NewReviewDTO("Great review!",game);
        Review review = new Review(user, newReviewDTO);

        UserReviewInteractionDTO dto = new UserReviewInteractionDTO();
        dto.setUser(user);
        dto.setReview(review);
        dto.setInteraction(ReviewInteraction.LIKE);

        UserReviewInteraction userReviewInteraction = new UserReviewInteraction(dto);

        assertNotNull(userReviewInteraction);
        assertEquals(user, userReviewInteraction.getUser());
        assertEquals(review, userReviewInteraction.getReview());
        assertEquals(ReviewInteraction.LIKE, userReviewInteraction.getInteraction());
    }
}