package com.goodmusic.goodmusicapp.service;

import com.goodmusic.goodmusicapp.dto.Response;
import com.goodmusic.goodmusicapp.dto.UserLoginRequestDTO;
import org.springframework.stereotype.Service;

@Service
public interface IUserService
{
    Response createUser(UserLoginRequestDTO userLoginRequestDTO);

    Response getAuthorizationUrlForUser(String userEmail);
}
