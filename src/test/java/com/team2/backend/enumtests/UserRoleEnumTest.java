package com.team2.backend.enumtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.team2.backend.enums.UserRole;
import com.team2.backend.exceptions.InvalidEnumValueException;

public class UserRoleEnumTest {
    @Test
    void testValidEnumValues() {
        assertEquals(UserRole.CONTRIBUTOR, UserRole.fromString("CONTRIBUTOR"));
        assertEquals(UserRole.MODERATOR, UserRole.fromString("MODERATOR"));
    }

    @Test
    void testValidEnumValuesIgnoreCase() {
        assertEquals(UserRole.CONTRIBUTOR, UserRole.fromString("contributor"));
        assertEquals(UserRole.MODERATOR, UserRole.fromString("moderator"));
    }

    @Test
    void testNullInput() {
        assertNull(UserRole.fromString(null), "Expected null for null input.");
    }

    @Test
    void testInvalidEnumValue() {
        InvalidEnumValueException exception = assertThrows(InvalidEnumValueException.class, () -> {
            UserRole.fromString("INVALID");
        });

        assertEquals("Invalid user role. Value must be CONTRIBUTOR or MODERATOR", exception.getMessage());
    }

    @Test
    void testEnumValueProperty() {
        assertEquals("CONTRIBUTOR", UserRole.CONTRIBUTOR.toString());
        assertEquals("MODERATOR", UserRole.MODERATOR.toString());
    }
}
