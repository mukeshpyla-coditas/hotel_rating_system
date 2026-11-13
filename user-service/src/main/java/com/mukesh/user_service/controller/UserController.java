package com.mukesh.user_service.controller;

import com.mukesh.user_service.dto.request.CreateUserRequestDto;
import com.mukesh.user_service.entity.User;
import com.mukesh.user_service.payload.ApiResponse;
import com.mukesh.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody @Valid CreateUserRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User created successfully",
                        userService.createUser(request)
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Fetched user successfully",
                                userService.getUserDetails(id)
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Fetched users successfully",
                                userService.getAllUsers()
                        )
                );
    }
}
