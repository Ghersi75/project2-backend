package com.team2.backend.Enums;

public enum UserRole {
    
        CONTRIBUTOR("CONTRIBUTOR"),
        MODERATOR("MODERATOR"),;

        private final String value;
        
        UserRole(String value) {
            this.value = value;
          }

          public static UserRole fromString(String value) throws Exception {
            if (value == null) {
              return null;
            }
        
            for (UserRole userRole : UserRole.values()) {
              if (userRole.value.equalsIgnoreCase(value)) {
                return userRole;
              }
            }
        
            throw new Exception("Invalid user role. Value must be Manager or Employee, case insensitive");
          }
    
}
