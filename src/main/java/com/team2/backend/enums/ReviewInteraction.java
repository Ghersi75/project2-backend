package com.team2.backend.enums;

import com.team2.backend.exceptions.*;

public enum ReviewInteraction {
  LIKE("LIKE"),
  DISLIKE("DISLIKE");

  private final String value;

  ReviewInteraction(String value) {
    this.value = value;
  }

  public static ReviewInteraction fromString(String value) {
    if (value == null) {
      return null;
    }

    for (ReviewInteraction reviewInteraction : ReviewInteraction.values()) {
      if (reviewInteraction.value.equalsIgnoreCase(value)) {
        return reviewInteraction;
      }
    }

    throw new InvalidEnumValueException("Invalid review. Value must be LIKE or DISLIKE");
  }
}
