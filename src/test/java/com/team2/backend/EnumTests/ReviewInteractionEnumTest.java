package com.team2.backend.EnumTests;

import com.team2.backend.enums.ReviewInteraction;
import com.team2.backend.exceptions.InvalidEnumValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewInteractionEnumTest {

    @Test
    void testValidEnumValues() {
        assertEquals(ReviewInteraction.LIKE, ReviewInteraction.fromString("LIKE"));
        assertEquals(ReviewInteraction.DISLIKE, ReviewInteraction.fromString("DISLIKE"));
    }

    @Test
    void testValidEnumValuesIgnoreCase() {
        assertEquals(ReviewInteraction.LIKE, ReviewInteraction.fromString("like"));
        assertEquals(ReviewInteraction.DISLIKE, ReviewInteraction.fromString("dislike"));
    }

    @Test
    void testNullInput() {
        assertNull(ReviewInteraction.fromString(null));
    }

    @Test
    void testInvalidEnumValue() {
        InvalidEnumValueException exception = assertThrows(InvalidEnumValueException.class, () -> {
            ReviewInteraction.fromString("INVALID");  // Should throw exception for invalid value
        });
        assertEquals("Invalid review. Value must be LIKE or DISLIKE", exception.getMessage());
    }

    @Test
    void testEnumValues() {
        assertTrue(ReviewInteraction.LIKE.toString().equals("LIKE"));
        assertTrue(ReviewInteraction.DISLIKE.toString().equals("DISLIKE"));
    }
}