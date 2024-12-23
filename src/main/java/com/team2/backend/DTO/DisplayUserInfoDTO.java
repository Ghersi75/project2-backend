package com.team2.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.team2.backend.Enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayUserInfoDTO {
    private Long userId;
    private String displayName;
    private String username;
    private UserRole userRole;
}
