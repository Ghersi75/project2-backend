package com.team2.backend.enumtests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.team2.backend.enums.NotificationType;
import com.team2.backend.exceptions.InvalidEnumValueException;

public class NotificationTypeTest {

    @Test
    void testFromString_ValidValues() {
        NotificationType like = NotificationType.fromString("LIKE");
        assertEquals(NotificationType.LIKE, like);

        NotificationType dislike = NotificationType.fromString("DISLIKE");
        assertEquals(NotificationType.DISLIKE, dislike);

        NotificationType review = NotificationType.fromString("REVIEW");
        assertEquals(NotificationType.REVIEW, review);
    }

    @Test
    void testFromString_InvalidValue() {
        assertNull(NotificationType.fromString(null));

        Exception exception = assertThrows(InvalidEnumValueException.class, () -> {
            NotificationType.fromString("INVALID");
        });
        assertEquals("Invalid notification. Value must be LIKE, DISLIKE, or REVIEW", exception.getMessage());
    }

    @Test
    void testFromString_CaseInsensitive() {
        NotificationType like = NotificationType.fromString("like");
        assertEquals(NotificationType.LIKE, like);

        NotificationType dislike = NotificationType.fromString("Dislike");
        assertEquals(NotificationType.DISLIKE, dislike);

        NotificationType review = NotificationType.fromString("ReVieW");
        assertEquals(NotificationType.REVIEW, review);
    }
}