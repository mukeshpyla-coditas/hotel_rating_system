package com.mukesh.auth_server.service.impl;

import com.mukesh.auth_server.dto.request.CreateUserRecordRequestDto;
import com.mukesh.auth_server.dto.request.LoginUserRequestDto;
import com.mukesh.auth_server.dto.response.LoginUserResponseDto;
import com.mukesh.auth_server.dto.response.ValidateTokenResponseDto;
import com.mukesh.auth_server.entity.AuthUser;
import com.mukesh.auth_server.enums.Role;
import com.mukesh.auth_server.exception.InvalidTokenException;
import com.mukesh.auth_server.exception.UserRecordAlreadyExistsException;
import com.mukesh.auth_server.repository.AuthUserRepository;
import com.mukesh.auth_server.service.AuthService;
import com.mukesh.auth_server.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuthUserRepository authUserRepository;

    @Override
    public void registerUser(CreateUserRecordRequestDto request) {
        if (authUserRepository.existsByUsername(request.username())
                || authUserRepository.existsByUserId(request.userId())) {
            throw new UserRecordAlreadyExistsException("A record for this user already exists.");
        }
        AuthUser newUserRecord = AuthUser.builder()
                .userId(request.userId())
                .hashedPassword(passwordEncoder.encode(request.originalPassword()))
                .role(Role.USER)
                .username(request.username())
                .build();
        authUserRepository.save(newUserRecord);
        log.info("A new user record, for the user with ID-{}, is created on the auth-service", request.userId());
    }

    @Override
    public LoginUserResponseDto loginUser(LoginUserRequestDto request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException exception) {
            throw new BadCredentialsException("Bad Credentials");
        }

        log.info("User is authenticated successfully by the auth-server.");
        AuthUser requestedUser = authUserRepository.findByUsername(request.username()).orElseThrow();
        String accessToken = jwtUtil.generateJwtToken(requestedUser.getUsername(), requestedUser.getUserId(), requestedUser.getRole().name());
        return LoginUserResponseDto.builder()
                .accessToken(accessToken)
                .userId(requestedUser.getUserId())
                .message("User is logged in successfully! Please note that this accessToken will be active only for next 15min. Once token is expired, user must re-login.")
                .build();
    }

    @Override
    public ValidateTokenResponseDto validateToken(String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) throw new InvalidTokenException("Token provided is not valid. Please check and retry again.");

        String token = authorizationHeader.substring(7);
        try {
            Claims claims = jwtUtil.extractClaims(token);
            return ValidateTokenResponseDto.builder()
                    .userId(claims.get("userId", Long.class))
                    .role(claims.get("role", String.class))
                    .username(claims.getSubject())
                    .build();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidTokenException("Token is invalid or expired!");
        }
    }
}
