package com.team2.backend.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.team2.backend.enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayUserInfoDTO {
    private Long userId;
    private String displayName;
    private String username;
    private UserRole userRole;
}
