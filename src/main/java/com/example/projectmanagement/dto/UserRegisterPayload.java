package com.example.projectmanagement.dto;


import com.example.projectmanagement.model.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterPayload {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public UserEntity toModel(){
        return UserEntity.builder()
                .userName(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
