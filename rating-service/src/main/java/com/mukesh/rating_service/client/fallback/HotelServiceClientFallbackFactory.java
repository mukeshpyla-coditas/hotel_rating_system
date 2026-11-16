package com.mukesh.rating_service.client.fallback;

import com.mukesh.rating_service.client.HotelServiceClient;
import com.mukesh.rating_service.payload.ApiResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HotelServiceClientFallbackFactory implements FallbackFactory<HotelServiceClient> {
    @Override
    public HotelServiceClient create(Throwable cause) {
        return id -> {
            // A 4xx is a real answer (e.g. 404): pass it on, don't hide it
            if (cause instanceof FeignException fe && fe.status() >= 400 && fe.status() < 500) {
                throw fe;
            }
            log.warn("Hotel service unavailable, using fallback. Cause: {}", cause.toString());
            return ApiResponse.failure(503, "Hotel service is currently unavailable");
        };
    }
}
