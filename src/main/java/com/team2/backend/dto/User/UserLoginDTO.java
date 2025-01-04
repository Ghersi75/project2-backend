package com.team2.backend.dto.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message = "Username cannot be blank")
    private String username;
  
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
