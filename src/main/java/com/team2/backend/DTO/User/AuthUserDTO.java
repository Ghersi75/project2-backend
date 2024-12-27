package com.team2.backend.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.team2.backend.Enums.UserRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDTO {
    private Long userId;
    private UserRole userRole;
}
