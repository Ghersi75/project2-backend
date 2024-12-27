package com.team2.backend.DTO.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpDTO {
    @NotBlank(message = "Display name cannot be blank")
    private String displayName;

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Pattern(regexp = "CONTRIBUTOR|MODERATOR", message = "Role must be CONTRIBUTOR or MODERATOR")
    private String role;

}
