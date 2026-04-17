package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.UserProfileRequestDTO;
import com.RealState.Project.DTO.UserProfileResponseDTO;
import com.RealState.Project.Entity.Type.UserType;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Entity.UserProfile;
import com.RealState.Project.Exception.UserNotFoundException;
import com.RealState.Project.Repository.UserProfileRepository;
import com.RealState.Project.Repository.UserRepository;
import com.RealState.Project.Service.UserService;
import com.RealState.Project.Utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final UserProfileRepository userProfileRepository;

    @Override
    public void deleteUser(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setDeleted(true);

        userRepository.save(user);
    }

    @Override
    public UserProfileResponseDTO getUserById(Long id){

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();

        return UserProfileResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(profile.getName())
                .phone(profile.getPhone())
                .profileUrl(profile.getProfileURL())
                .userType(profile.getUserType().name())
                .isDeleated(user.isDeleted())
                .build();
    }

    @Override

    public UserProfileResponseDTO getCurrentUserProfile() {

            User user = securityUtil.getCurrentUser(); // already you have this

            if (user.isDeleted()) {
                throw new RuntimeException("User is deleted");
            }

            UserProfile profile = user.getUserProfile();

        return UserProfileResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(profile.getName())
                .phone(profile.getPhone())
                .profileUrl(profile.getProfileURL())
                .userType(profile.getUserType().name())
                .isDeleated(user.isDeleted())
                .build();
    }

    @Override
    public UserProfileResponseDTO createOrUpdateProfile(UserProfileRequestDTO dto) {
        User user = securityUtil.getCurrentUser();

        if (user.isDeleted()) {
            throw new RuntimeException("User is deleted");
        }

        UserProfile profile = user.getUserProfile();

        if (profile == null) {
            throw new UserNotFoundException("User ot Found");

        } else {
            // UPDATE
            profile.setName(dto.getName());
            profile.setPhone(dto.getPhone());
            profile.setProfileURL(dto.getProfileURL());
            profile.setArea(dto.getArea());
            profile.setCity(dto.getCity());
            profile.setPin(dto.getPin());
        }

        profile = userProfileRepository.save(profile);

        return UserProfileResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(profile.getName())
                .phone(profile.getPhone())
                .profileUrl(profile.getProfileURL())
                .userType(profile.getUserType().name())
                .isDeleated(user.isDeleted())
                .build();
    }

    public void softDeleteCurrentUser() {

        User user = securityUtil.getCurrentUser();

        user.setDeleted(true);

        userRepository.save(user);
    }
}
