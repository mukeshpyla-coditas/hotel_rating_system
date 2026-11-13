package com.mukesh.user_service.service;

import com.mukesh.user_service.dto.request.CreateUserRequestDto;
import com.mukesh.user_service.entity.User;

import java.util.List;

public interface UserService {
    String createUser(CreateUserRequestDto request);
    User getUserDetails(Long id);
    List<User> getAllUsers();
}
