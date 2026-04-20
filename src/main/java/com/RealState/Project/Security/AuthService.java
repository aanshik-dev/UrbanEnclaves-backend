package com.RealState.Project.Security;

import com.RealState.Project.DTO.Auth.*;
import com.RealState.Project.Entity.*;
import com.RealState.Project.Entity.Type.AuthProviderType;
import com.RealState.Project.Entity.Type.Status;
import com.RealState.Project.Entity.Type.UserType;
import com.RealState.Project.Exception.AccessDeniedException;
import com.RealState.Project.Repository.*;
import com.RealState.Project.Service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;
    private  final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    public final RefreshTokenService refreshTokenService;
    public final RefreshTokenRepository refreshTokenRepository;
    private final UserProfileRepository userProfileRepository;
    private final AgentRepository agentRepository;
    private final OfficeRepository officeRepository;

    private User signupInternal(SignupRequestDTO dto, AuthProviderType providerType, String providerId) {
        User user = userRepository.findByUsername(dto.getUsername()).orElse(null);
        if(user!=null) throw new IllegalArgumentException("User already Exist");

        user = userRepository.save(User.builder()
                .username(dto.getUsername())
                .providerId(providerId)
                .providerType(providerType)
                .email(dto.getEmail())
                .build()
        );

        if(providerType == AuthProviderType.EMAIL)
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);

        return user;

    }

    public void sendSignupOtp(String email){

        User user = userRepository.findByEmail(email).orElse(null);

        if(user != null)
            throw new IllegalArgumentException("Email already registered");

        otpService.sendOtp(email);
    }

    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO dto){

        User user = userRepository
                .findByUsername(dto.getUsername())
                .orElse(null);

        if(user != null){
            throw new IllegalArgumentException(
                    "User already exists"
            );
        }

        boolean verified =
                otpService.verifyOtp(
                        dto.getEmail(),
                        dto.getOtp()
                );

        if(!verified){
            throw new RuntimeException(
                    "Email not verified"
            );
        }

        UserType requestedType =
                UserType.valueOf(
                        String.valueOf(dto.getUserType())
                );

        // only USER / AGENT allowed
        if(requestedType != UserType.USER &&
                requestedType != UserType.AGENT){

            throw new RuntimeException(
                    "Invalid signup role"
            );
        }

        user = signupInternal(
                dto,
                AuthProviderType.EMAIL,
                null
        );


        // CREATE PROFILE

        UserProfile profile =
                UserProfile.builder()
                        .user(user)
                        .name(dto.getUsername())
                        .phone(0)
                        .profileURL(null)
                        .area("")
                        .city("")
                        .pin(0)
                        .userType(requestedType)
                        .build();

        userProfileRepository.save(profile);


        // IF AGENT CREATE AGENT ROW

        if(requestedType == UserType.AGENT){

            List<Office> offices =
                    officeRepository.findAll();

            if(offices.isEmpty()){
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

            Agent agent =
                    Agent.builder()
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

        return new SignupResponseDTO(
                user.getId(),
                user.getUsername()
        );
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(),dto.getPassword())
        );

        User user = (User)authentication.getPrincipal();

        if(user.getUserProfile() == null){
            throw new RuntimeException(
                    "Profile not found"
            );
        }

        if (user.getUserProfile().getUserType() != dto.getUserType()) {
            throw new AccessDeniedException(
                    "Invalid login type. You are not " + dto.getUserType()
            );
        }
        String token = authUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user);

        return new LoginResponseDTO(token,refreshToken.getToken(),user.getUsername(),user.getId());
    }

    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO dto){

        RefreshToken oldToken =
                refreshTokenService.verifyRefreshToken(dto.getRefreshToken());

        User user = oldToken.getUser();

        refreshTokenRepository.delete(oldToken);

        RefreshToken newToken =
                refreshTokenService.createOrUpdateRefreshToken(user);

        String accessToken = authUtil.generateToken(user);

        return new LoginResponseDTO(
                accessToken,
                newToken.getToken(),
                user.getUsername(),
                user.getId()
        );
    }

    public ResponseEntity<LoginResponseDTO> handlerOAuth2LoginRequest(OAuth2User oAuth2User, String registerId) {

        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registerId);
        String providerId = authUtil.determineProviderIdFromAuth2User(oAuth2User , registerId);

        User user = userRepository.findByProviderIdAndProviderType(providerId,providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute(("name"));
        User emailUser = userRepository.findByEmail(email).orElse(null);

        if(user == null && emailUser == null) {
            String username = authUtil.determineUsernameFromAuth2User(oAuth2User,registerId,providerId);

            user = signupInternal(
                    new SignupRequestDTO(username , null , email,null,null),
                    providerType,
                    providerId
            );
        }

        else if(user != null){
            if(email != null && !email.isBlank() && !email.equals(user.getUsername())) {
                user.setEmail(email);
                userRepository.save(user);
            }
        }

        else if(emailUser != null){
            // LINK ACCOUNT
            emailUser.setProviderType(providerType);
            emailUser.setProviderId(providerId);

            user = userRepository.save(emailUser);
        }

        LoginResponseDTO loginResponseDTO  = new LoginResponseDTO(authUtil.generateToken(user),
                refreshTokenService.createOrUpdateRefreshToken(user).getToken()
                ,user.getUsername(),user.getId());

        return ResponseEntity.ok(loginResponseDTO);

    }


    public void resetPassword(ResetPasswordDTO dto){

        if(!dto.getNewPassword().equals(dto.getConfirmPassword()))
            throw new RuntimeException("Password mismatch");

        User user = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        otpService.verifyOtp(dto.getEmail(), dto.getOtp());

        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword())
        );

        userRepository.save(user);
    }

    public void forgotPassword(ForgetPasswordDTO dto){

        User user = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        otpService.sendOtp(dto.getEmail());
    }

    public void changePassword(ChangePasswordDTO dto){

        User user = authUtil.getCurrentUser();

        if(!passwordEncoder.matches(
                dto.getOldPassword(),
                user.getPassword()
        )){
            throw new RuntimeException("Old password incorrect");
        }

        if(!dto.getNewPassword()
                .equals(dto.getConfirmPassword())){
            throw new RuntimeException("Password mismatch");
        }

        if(passwordEncoder.matches(
                dto.getNewPassword(),
                user.getPassword())){
            throw new RuntimeException(
                    "New password must be different"
            );
        }

        user.setPassword(
                passwordEncoder.encode(dto.getNewPassword())
        );

        userRepository.save(user);
    }



}
