package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.UserProfileRequestDTO;
import com.RealState.Project.DTO.UserProfileResponseDTO;
import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.Office;
import com.RealState.Project.Entity.Type.Status;
import com.RealState.Project.Entity.Type.UserType;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Entity.UserProfile;
import com.RealState.Project.Exception.UserNotFoundException;
import com.RealState.Project.Repository.AgentRepository;
import com.RealState.Project.Repository.OfficeRepository;
import com.RealState.Project.Repository.UserProfileRepository;
import com.RealState.Project.Repository.UserRepository;
import com.RealState.Project.Service.UserService;
import com.RealState.Project.Utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final UserProfileRepository userProfileRepository;
    private final AgentRepository agentRepository;
    private final OfficeRepository officeRepository;

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
                .area(profile.getArea())
                .city(profile.getCity())
                .pin(profile.getPin())
                .regDate(profile.getRegDate())
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
                .area(profile.getArea())
                .city(profile.getCity())
                .pin(profile.getPin())
                .regDate(profile.getRegDate())
                .phone(profile.getPhone())
                .profileUrl(profile.getProfileURL())
                .userType(profile.getUserType().name())
                .isDeleated(user.isDeleted())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponseDTO createOrUpdateProfile(
            UserProfileRequestDTO dto
    ) {

        User user = securityUtil.getCurrentUser();

        if (user.isDeleted()) {
            throw new RuntimeException("User is deleted");
        }

        UserProfile profile = user.getUserProfile();

        // =====================================
        // DEFAULT USER TYPE
        // =====================================
        UserType requestedType = UserType.USER;

        if (dto.getUserType() != null &&
                !dto.getUserType().isBlank()) {

            requestedType =
                    UserType.valueOf(
                            dto.getUserType()
                                    .toUpperCase()
                    );
        }

        // =====================================
        // CREATE PROFILE
        // =====================================
        if (profile == null) {

            profile = UserProfile.builder()
                    .user(user)
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .profileURL(dto.getProfileURL())
                    .area(dto.getArea())
                    .city(dto.getCity())
                    .pin(dto.getPin())
                    .userType(requestedType)
                    .build();

        } else {

            // =====================================
            // UPDATE ONLY DTO FIELDS
            // =====================================
            profile.setName(dto.getName());
            profile.setPhone(dto.getPhone());
            profile.setProfileURL(dto.getProfileURL());
            profile.setArea(dto.getArea());
            profile.setCity(dto.getCity());
            profile.setPin(dto.getPin());

            if (dto.getUserType() != null &&
                    !dto.getUserType().isBlank()) {

                profile.setUserType(requestedType);
            }
        }

        profile = userProfileRepository.save(profile);

        // =====================================
        // CREATE AGENT ROW IF TYPE = AGENT
        // =====================================
        if (profile.getUserType() == UserType.AGENT &&
                !agentRepository.existsById(user.getId())) {

            List<Office> offices =
                    officeRepository.findAll();

            if (offices.isEmpty()) {
                throw new RuntimeException(
                        "No office available"
                );
            }

            Office randomOffice =
                    offices.get(
                            new Random().nextInt(
                                    offices.size()
                            )
                    );

            Agent agent = Agent.builder()
                    .user(user)
                    .commissionRate(2.5f)
                    .licenceNumber(
                            "LIC-" + user.getId()
                    )
                    .experience(0)
                    .status(Status.ACTIVE)
                    .office(randomOffice)
                    .build();

            agentRepository.save(agent);
        }

        return UserProfileResponseDTO.builder()

                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())

                .name(profile.getName())
                .phone(profile.getPhone())
                .profileUrl(profile.getProfileURL())

                .isDeleated(user.isDeleted())

                .area(profile.getArea())
                .city(profile.getCity())
                .pin(profile.getPin())

                .regDate(profile.getRegDate())
                .userType(profile.getUserType().name())

                .build();
    }

    @Override
    public void softDeleteCurrentUser() {

        User user = securityUtil.getCurrentUser();

        user.setDeleted(true);

        userRepository.save(user);
    }
}
