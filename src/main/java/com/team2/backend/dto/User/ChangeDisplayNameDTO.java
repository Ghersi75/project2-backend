package com.team2.backend.dto.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDisplayNameDTO {
     private String newDisplayName;
     private String password;

}
