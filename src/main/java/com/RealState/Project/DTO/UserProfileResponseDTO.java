package com.RealState.Project.DTO;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponseDTO {

    private Long userId;
    private String username;
    private String email;

    private String name;
    private long phone;
    private String profileUrl;
    private boolean isDeleated;
    private String area;
    private String city;
    private int pin;
    private LocalDate regDate;
    private String userType;
}
