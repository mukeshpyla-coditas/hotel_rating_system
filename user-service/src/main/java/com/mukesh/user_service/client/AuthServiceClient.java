package com.mukesh.user_service.client;

import com.mukesh.user_service.dto.request.CreateUserRecordOnAuthServerRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "AUTH-SERVER"
)
public interface AuthServiceClient {
    @PostMapping("/api/v1/auth")
    void createUserRecordOnAuthServer(CreateUserRecordOnAuthServerRequestDto request);
}
