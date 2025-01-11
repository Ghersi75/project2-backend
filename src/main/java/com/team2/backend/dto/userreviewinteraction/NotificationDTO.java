package com.team2.backend.dto.userreviewinteraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long reviewId;  
    private Long appId;
    private String gameName;
    private String username;
}
