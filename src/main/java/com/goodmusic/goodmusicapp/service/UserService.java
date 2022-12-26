package com.goodmusic.goodmusicapp.service;

import com.goodmusic.goodmusicapp.constants.ResponseCodes;
import com.goodmusic.goodmusicapp.dto.*;
import com.goodmusic.goodmusicapp.model.User;
import com.goodmusic.goodmusicapp.repository.UserRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class UserService implements IUserService
{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment env;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gson;


    @Override
    public Response createUser(UserLoginRequestDTO userLoginRequestDTO) {

        // Check if user already exist
        User alreadySavedUser = userRepository.findByEmail(userLoginRequestDTO.getEmail());
        if(alreadySavedUser != null){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setResponseCode(ResponseCodes.RECORD_ALREADY_EXIST.getCode());
            errorResponse.setResponseMessage(ResponseCodes.RECORD_ALREADY_EXIST.name());
            return errorResponse;
        }

        // Create a user model
        User user = new User();
        user.setUsername(userLoginRequestDTO.getUsername());
        user.setEmail(userLoginRequestDTO.getEmail());
        user.setPassword(userLoginRequestDTO.getPassword());
        // The access token and refresh token and state will be stored later after authorization.

        // Call the repository class or layer to save the user to database.
        User savedUser =  userRepository.saveAndFlush(user);

        PayloadResponse response = new PayloadResponse();
        response.setResponseCode(ResponseCodes.SUCCESS.getCode());
        response.setResponseMessage(ResponseCodes.SUCCESS.name());
        response.setData(savedUser);
        return response;
    }

    @Override
    public Response getAuthorizationUrlForUser(String userEmail) {

        // Build the authorization Url
        String clientId = env.getProperty("sportify.client.client-id");
        String clientSecret = env.getProperty("sportify.client.client-secret");
        String redirectUrl = env.getProperty("sportify.client.redirect-uri");
        String scope = env.getProperty("sportify.client.scope");
        String authorizationUrl = env.getProperty("sportify.client.authorization-url");
        String responseType = env.getProperty("sportify.client.response-type");

        String completeUrl = authorizationUrl +
                "response_type=" + responseType +
                "&client_id=" + clientId +
                "&scope=" + scope +
                "&redirect_uri=" + redirectUrl +
                "&state=" + userEmail;

        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setResponseCode(ResponseCodes.SUCCESS.getCode());
        payloadResponse.setResponseMessage(ResponseCodes.SUCCESS.name());
        payloadResponse.setData(completeUrl);
        return payloadResponse;
    }


    public Response getAuthorizationTokens(String code, String state){
        String userEmail = state;
        String redirectUrl = env.getProperty("sportify.client.redirect-uri");
        String clientId = env.getProperty("sportify.client.client-id");
        String clientSecret = env.getProperty("sportify.client.client-secret");

        // Make a call to sportify to get the access and the refresh token.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("redirect_uri", redirectUrl);
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        String url = env.getProperty("sportify.client.authorization-token-url");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(form, headers);

        String responseJson = null;
        HttpStatus status = HttpStatus.CONFLICT;
        int statusValue = 0;
        String errorPayload = null;
        String errorMessage = null;

        try{
            ResponseEntity<String> responseEntity = restTemplate
                    .exchange(url, HttpMethod.POST, requestEntity, String.class);
            if(responseEntity != null){
                String responseBody = responseEntity.getBody();
                if(responseBody != null){
                    System.out.println("HttpClientConnectionSuccessResponse: " + responseBody);
                    responseJson = responseBody;
                    status = responseEntity.getStatusCode();
                    statusValue = responseEntity.getStatusCodeValue();
                }
            }
        }catch (HttpClientErrorException | HttpServerErrorException ex){
            if( ex instanceof HttpClientErrorException){
                System.out.println("ClientConnectionError: " +
                        ((HttpClientErrorException) ex).getResponseBodyAsString());
                errorPayload = ((HttpClientErrorException) ex).getResponseBodyAsString();
                errorMessage = ((HttpClientErrorException) ex).getMessage();
            }
            if(ex instanceof HttpServerErrorException){
                System.out.println("HttpThirdPartyServerError: " +
                        ((HttpServerErrorException) ex).getResponseBodyAsString());
                errorPayload = ((HttpServerErrorException) ex).getResponseBodyAsString();
                errorMessage = ((HttpServerErrorException) ex).getMessage();
            }
            System.out.println("ConnectionError: " + ex.getResponseBodyAsString());
            System.out.println("ErrorMessage: " + ex.getMessage());
            ex.printStackTrace();
            responseJson = null;
        }

        if(responseJson == null){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setResponseCode(ResponseCodes.THIRD_PARTY_ERROR.getCode());
            errorResponse.setResponseMessage(String.join("|", errorPayload, errorMessage));
            return errorResponse;
        }

        if(status.equals(HttpStatus.OK) || statusValue == HttpStatus.OK.value()){
            SportifyTokenPayload tokenPayload = gson.fromJson(responseJson, SportifyTokenPayload.class);
            // Get the user that has this token
            User currentUser = userRepository.findByEmail(userEmail);
            currentUser.setAccessToken(tokenPayload.getAccessToken());
            currentUser.setRefreshToken(tokenPayload.getRefreshToken());
            User updateUser = userRepository.saveAndFlush(currentUser);

            PayloadResponse payloadResponse = new PayloadResponse();
            payloadResponse.setResponseCode(ResponseCodes.SUCCESS.getCode());
            payloadResponse.setResponseMessage(ResponseCodes.SUCCESS.name());
            payloadResponse.setData(updateUser);
            return payloadResponse;
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setResponseCode(ResponseCodes.FAILED_MODEL.getCode());
        errorResponse.setResponseMessage(responseJson);
        return errorResponse;
    }

}
