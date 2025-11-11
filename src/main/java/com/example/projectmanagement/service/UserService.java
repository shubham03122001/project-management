package com.example.projectmanagement.service;


import com.example.projectmanagement.dto.GetAllUsersResponseDto;
import com.example.projectmanagement.dto.LoginRequestDto;
import com.example.projectmanagement.dto.UserRegisterPayload;
import com.example.projectmanagement.dto.UserSuccessfullyLoginDto;
import com.example.projectmanagement.exceptions.UserAlreadyExistsException;
import com.example.projectmanagement.exceptions.WrongCredentialException;
import com.example.projectmanagement.model.UserEntity;
import com.example.projectmanagement.repository.UserRepository;
import com.example.projectmanagement.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private  final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    private final JwtUtility jwtUtility;


    public void registeruser(UserRegisterPayload payload){
        if(userRepository.existsByEmail(payload.getEmail())){
            logger.warn("User with emailId: {} already exists", payload.getEmail());
            throw new UserAlreadyExistsException("User with email "+payload.getEmail()+" already exists");
        }

        UserEntity userData = payload.toModel();

        userData.setPassword(passwordEncoder.encode(payload.getPassword()));
        userRepository.save(userData);
        logger.info("User registered successfully with email: {}", payload.getEmail());
    }

    public UserSuccessfullyLoginDto verifyUser(LoginRequestDto requestDto){
        logger.info("Entering verifyUser() with email: {}", requestDto.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword()
                    )
            );
            logger.info("Authentication successful for email: {}", requestDto.getEmail());
        } catch (Exception ex) {
            logger.error("Authentication failed for email: {}", requestDto.getEmail(), ex);
            throw new WrongCredentialException("Invalid email or password");
        }

        //generate token

        String token = jwtUtility.generateToken(requestDto.getEmail());
        logger.info("JWT token generated for email: {}", requestDto.getEmail());

        UserSuccessfullyLoginDto response = new UserSuccessfullyLoginDto("Login successful", token);
        logger.debug("Login response DTO: {}", response);

        return response;
    }

    public List<GetAllUsersResponseDto> getAllUsers(){
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> GetAllUsersResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUserName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());


    }
}
