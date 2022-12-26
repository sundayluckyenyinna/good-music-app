package com.goodmusic.goodmusicapp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserLoginRequestDTO
{
    @NotNull(message = "username cannot be null")
    @NotEmpty(message = "username cannot be empty")
    @NotBlank(message = "username cannot be blank")
    private String username;

    @NotNull(message = "email cannot be null")
    @NotEmpty(message = "email cannot be empty")
    @NotBlank(message = "email cannot be blank")
    private String email;

    @NotNull(message = "password cannot be null")
    @NotEmpty(message = "password cannot be empty")
    @NotBlank(message = "password cannot be blank")
    private String password;

}
