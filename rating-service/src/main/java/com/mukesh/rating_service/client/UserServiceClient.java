package com.mukesh.rating_service.client;

import com.mukesh.rating_service.dto.response.UserServiceResponseDto;
import com.mukesh.rating_service.payload.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "USER-SERVICE"
)
public interface UserServiceClient {
    @GetMapping("/api/v1/user/{id}")
    ApiResponse<UserServiceResponseDto> getUserById(@PathVariable(name = "id") Long id);
}
