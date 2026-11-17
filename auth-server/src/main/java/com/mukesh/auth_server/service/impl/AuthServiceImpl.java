package com.mukesh.auth_server.service.impl;

import com.mukesh.auth_server.dto.CreateUserRecordRequestDto;
import com.mukesh.auth_server.entity.AuthUser;
import com.mukesh.auth_server.enums.Role;
import com.mukesh.auth_server.repository.AuthUserRepository;
import com.mukesh.auth_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;

    @Override
    public void registerUser(CreateUserRecordRequestDto request) {
        AuthUser newUserRecord = AuthUser.builder()
                .userId(request.userId())
                .hashedPassword(passwordEncoder.encode(request.originalPassword()))
                .role(Role.USER)
                .username(request.username())
                .build();
        authUserRepository.save(newUserRecord);
        log.info("A new user record, for the user with ID-{}, is created on the auth-service", request.userId());
    }
}
