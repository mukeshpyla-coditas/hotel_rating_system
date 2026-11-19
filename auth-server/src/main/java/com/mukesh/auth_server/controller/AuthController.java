package com.mukesh.auth_server.controller;

import com.mukesh.auth_server.dto.request.CreateUserRecordRequestDto;
import com.mukesh.auth_server.dto.request.LoginUserRequestDto;
import com.mukesh.auth_server.dto.response.LoginUserResponseDto;
import com.mukesh.auth_server.dto.response.ValidateTokenResponseDto;
import com.mukesh.auth_server.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> createUserRecord(@RequestBody @Valid CreateUserRecordRequestDto request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDto> loginUser(@RequestBody @Valid LoginUserRequestDto request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponseDto> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader)  {
        return ResponseEntity.ok(authService.validateToken(authorizationHeader));
    }
}
