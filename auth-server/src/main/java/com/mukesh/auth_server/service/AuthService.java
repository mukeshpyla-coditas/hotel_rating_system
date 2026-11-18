package com.mukesh.auth_server.service;

import com.mukesh.auth_server.dto.request.CreateUserRecordRequestDto;
import com.mukesh.auth_server.dto.request.LoginUserRequestDto;
import com.mukesh.auth_server.dto.response.LoginUserResponseDto;
import com.mukesh.auth_server.dto.response.ValidateTokenResponseDto;

public interface AuthService {
    void registerUser(CreateUserRecordRequestDto request);
    LoginUserResponseDto loginUser(LoginUserRequestDto request);
    ValidateTokenResponseDto validateToken(String authorizationHeader);
}
