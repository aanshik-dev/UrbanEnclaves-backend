package com.RealState.Project.DTO.Auth;

import com.RealState.Project.Entity.Type.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class SignupRequestDTO {

    private String username;
    private String password;
    private String email;
    private String otp;
    private UserType userType;
}
