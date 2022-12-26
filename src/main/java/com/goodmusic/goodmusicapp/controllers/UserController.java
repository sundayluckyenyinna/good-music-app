package com.goodmusic.goodmusicapp.controllers;

import com.goodmusic.goodmusicapp.dto.Response;
import com.goodmusic.goodmusicapp.dto.UserLoginRequestDTO;
import com.goodmusic.goodmusicapp.model.User;
import com.goodmusic.goodmusicapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController
{

    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(
            @RequestBody @Valid UserLoginRequestDTO userLoginRequestDTO,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    )
    {
        // Call the service to save the user
        Response response = userService.createUser(userLoginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authorizationUrl")
    public ResponseEntity<Response> getCompleteAuthorizationUrl(
        HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse,
        @RequestParam("email") String email
    )
    {
        // Call the service to get the complete Url
        Response response = userService.getAuthorizationUrlForUser(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sportify-authorize")
    public ResponseEntity<Response> getAuthorizationCode(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    )
    {
        // Call the service to make a call to sportify to get the access token and the refresh token and save
        return null;
    }
}
