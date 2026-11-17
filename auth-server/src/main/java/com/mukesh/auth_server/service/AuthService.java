package com.mukesh.auth_server.service;

import com.mukesh.auth_server.dto.CreateUserRecordRequestDto;

public interface AuthService {
    void registerUser(CreateUserRecordRequestDto request);
}
