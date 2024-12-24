package com.team2.backend.Enums;

import com.team2.backend.Exceptions.*;

public enum ReviewInteraction {
    LIKED("LIKED"),
    DISLIKED("DISLIKED");
  
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
  
      throw new InvalidEnumValueException("Invalid review. Value must be LIKED or DISLIKED");
    }
}
