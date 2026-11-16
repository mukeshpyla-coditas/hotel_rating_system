package com.mukesh.rating_service.controller;

import com.mukesh.rating_service.dto.request.CreateRatingRequestDto;
import com.mukesh.rating_service.dto.request.UpdateRatingRequestDto;
import com.mukesh.rating_service.dto.response.GetRatingDetailsResponseDto;
import com.mukesh.rating_service.entity.Rating;
import com.mukesh.rating_service.payload.ApiResponse;
import com.mukesh.rating_service.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createRating(@RequestBody @Valid CreateRatingRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                HttpStatus.CREATED.value(),
                                "Rating created successfully!",
                                ratingService.createRating(request)
                        )
                );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rating>> getRatingById(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Fetched the requested rating details!",
                        ratingService.getRatingById(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Rating>>> getAllRatings() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Fetched all rating details, successfully!",
                        ratingService.getAllRatings()
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getAllRatingsSubmittedByAUser(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Fetched the ratings submitted by requested user",
                        ratingService.getAllRatingsSubmittedByUser(userId)
                )
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getAllRatingsOfAHotel(@PathVariable(name = "hotelId") Long hotelId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Fetched all the ratings of requested hotel",
                        ratingService.getAllRatingsOfAHotel(hotelId)
                )
        );
    }

    @PatchMapping("/update/{ratingId}")
    public ResponseEntity<ApiResponse<Rating>> updateRating(
            @PathVariable(name = "ratingId") Long ratingId,
            @RequestBody @Valid UpdateRatingRequestDto request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Updated the details of the requested rating!",
                        ratingService.updateRating(ratingId, request)
                )
        );
    }

    @GetMapping("/complete-details/{id}")
    public ResponseEntity<ApiResponse<GetRatingDetailsResponseDto>> getCompleteDetailsOfARating(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Fetched complete details of the requested rating(along with respective user and hotel details)",
                        ratingService.getAllDetailsOfARating(id)
                )
        );
    }
}
