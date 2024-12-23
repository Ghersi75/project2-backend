package com.team2.backend.Enums;

import com.team2.backend.Exceptions.*;

public enum UserRole {
  CONTRIBUTOR("CONTRIBUTOR"),
  MODERATOR("MODERATOR");

  private final String value;

  UserRole(String value) {
    this.value = value;
  }

  public static UserRole fromString(String value) {
    if (value == null) {
      return null;
    }

    for (UserRole userRole : UserRole.values()) {
      if (userRole.value.equalsIgnoreCase(value)) {
        return userRole;
      }
    }

    throw new InvalidEnumValueException("Invalid user role. Value must be CONTRIBUTOR or MODERATOR");
  }
}