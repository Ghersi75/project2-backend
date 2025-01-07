package com.team2.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "Username cannot be blank")
    private String username;
  
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
