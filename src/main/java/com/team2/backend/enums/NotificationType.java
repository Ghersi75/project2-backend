package com.team2.backend.enums;

import com.team2.backend.exceptions.InvalidEnumValueException;

public enum NotificationType {
    LIKE("LIKE"),
    DISLIKE("DISLIKE"),
    REVIEW("REVIEW");

    private final String value;

    NotificationType(String value) {
      this.value = value;
    }

    public static NotificationType fromString(String value) {
      if (value == null) {
        return null;
      }

      for (NotificationType notificationType : NotificationType.values()) {
        if (notificationType.value.equalsIgnoreCase(value)) {
          return notificationType;
        }
      }

      throw new InvalidEnumValueException("Invalid notification. Value must be LIKE, DISLIKE, or REVIEW");
    }
    
}
