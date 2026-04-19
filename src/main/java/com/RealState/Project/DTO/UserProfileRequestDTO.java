package com.RealState.Project.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileRequestDTO {
    String name;
    long phone;
    String profileURL;
    String area;
    String city;
    int pin;
    private String userType;
}