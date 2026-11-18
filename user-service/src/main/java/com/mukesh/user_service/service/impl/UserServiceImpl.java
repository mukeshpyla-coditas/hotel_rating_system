package com.mukesh.user_service.service.impl;

import com.mukesh.user_service.client.AuthServiceClient;
import com.mukesh.user_service.dto.request.CreateUserRecordOnAuthServerRequestDto;
import com.mukesh.user_service.dto.request.CreateUserRequestDto;
import com.mukesh.user_service.entity.User;
import com.mukesh.user_service.exception.ResourceNotFoundException;
import com.mukesh.user_service.exception.ServiceUnavailableException;
import com.mukesh.user_service.exception.UserAlreadyExistsException;
import feign.FeignException;
import com.mukesh.user_service.repository.UserRepository;
import com.mukesh.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public String createUser(CreateUserRequestDto request) {
        // Fail fast on a duplicate email. The unique constraint is the real guard for concurrent requests.
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }

        // Step-1: Create the user-record on user-service
        User createUser = User.builder()
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .contactNumber(request.contactNumber())
                .email(request.email())
                .about(request.about())
                .isActive(true)
                .build();
        userRepository.save(createUser);
        log.info("A new user with ID-{} is created successfully!", createUser.getId());

        // Step-2: Send the userID to auth-service to create a record of user on it.
        CreateUserRecordOnAuthServerRequestDto recordOnAuthServerRequest = CreateUserRecordOnAuthServerRequestDto.builder()
                .userId(createUser.getId())
                .username(createUser.getEmail())
                .originalPassword(request.password())
                .build();

        // Every catch below must THROW a RuntimeException so the transaction rolls back the user insert.
        try {
            authServiceClient.createUserRecordOnAuthServer(recordOnAuthServerRequest);
        } catch (FeignException.Conflict e) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        } catch (FeignException e) {
            // Log only the status: never the request (it contains the password).
            log.error("Call to auth-server failed for user ID-{}, status={}", createUser.getId(), e.status());
            throw new ServiceUnavailableException("Could not complete registration. Please try again later.");
        }
        log.info("Successfully created a record of current user of ID-{} on auth-server", createUser.getId());

        return "A user with ID-" + createUser.getId() + " is created!";
    }

    @Override
    public User getUserDetails(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with specified ID is not found. Please verify the ID and try again."));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
