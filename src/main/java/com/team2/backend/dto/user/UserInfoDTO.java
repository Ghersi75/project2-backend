package com.team2.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private DisplayUserInfoDTO displayUserInfoDTO;
    private AuthUserDTO authUserInfoDTO;
}