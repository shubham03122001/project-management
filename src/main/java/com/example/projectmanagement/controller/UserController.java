package com.example.projectmanagement.controller;


import com.example.projectmanagement.dto.*;
import com.example.projectmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/registeruser")
    public ResponseEntity<UserSuccessfullyRegisteredResponse> registerUser(@Valid @RequestBody UserRegisterPayload payload){

        userService.registeruser(payload);

        UserSuccessfullyRegisteredResponse registeredResponse = new UserSuccessfullyRegisteredResponse("User registered successfully");
        return new ResponseEntity<>(registeredResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserSuccessfullyLoginDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto){
        UserSuccessfullyLoginDto response = userService.verifyUser(loginRequestDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("/myprofile")
    public ResponseEntity<?> getMyProfile(Authentication authentication){
        return new ResponseEntity<>("You are logged in as:-"+authentication.getName(),HttpStatus.OK);
    }

    @GetMapping("getallusers")
    public ResponseEntity<List<GetAllUsersResponseDto>> getAllUsers(){
        List<GetAllUsersResponseDto> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers,HttpStatus.OK);
    }
}
