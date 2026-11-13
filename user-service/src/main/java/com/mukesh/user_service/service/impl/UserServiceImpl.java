package com.mukesh.user_service.service.impl;

import com.mukesh.user_service.dto.request.CreateUserRequestDto;
import com.mukesh.user_service.entity.User;
import com.mukesh.user_service.exception.ResourceNotFoundException;
import com.mukesh.user_service.repository.UserRepository;
import com.mukesh.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public String createUser(CreateUserRequestDto request) {
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
