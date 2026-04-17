package com.RealState.Project.Service;

import com.RealState.Project.DTO.UserProfileRequestDTO;
import com.RealState.Project.DTO.UserProfileResponseDTO;
import com.RealState.Project.Entity.User;
import org.jspecify.annotations.Nullable;

public interface UserService {
    public void deleteUser(Long userId);

    UserProfileResponseDTO getUserById(Long id);

    UserProfileResponseDTO getCurrentUserProfile();

    UserProfileResponseDTO createOrUpdateProfile(UserProfileRequestDTO dto);

    void softDeleteCurrentUser();
}
