package com.RealState.Project.Controller;


import com.RealState.Project.DTO.UserProfileRequestDTO;
import com.RealState.Project.DTO.UserProfileResponseDTO;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserById(
            @PathVariable Long id){

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }


    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PostMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> createOrUpdateProfile(
            @RequestBody UserProfileRequestDTO dto) {

        return ResponseEntity.ok(
                userService.createOrUpdateProfile(dto)
        );
    }


    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {

        userService.softDeleteCurrentUser();

        return ResponseEntity.noContent().build();
    }
}
