package com.team2.backend.dto.user;

import com.team2.backend.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayUserInfoDTO {
    private Long userId;
    private String displayName;
    private String username;
    private UserRole userRole;
}
