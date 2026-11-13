package com.mukesh.rating_service.service;

import com.mukesh.rating_service.dto.request.CreateRatingRequestDto;
import com.mukesh.rating_service.dto.request.UpdateRatingRequestDto;
import com.mukesh.rating_service.dto.response.GetRatingDetailsResponseDto;
import com.mukesh.rating_service.entity.Rating;

import java.util.List;

public interface RatingService {
    String createRating(CreateRatingRequestDto request);
    Rating getRatingById(Long id);
    List<Rating> getAllRatings();
    List<Rating> getAllRatingsSubmittedByUser(Long userId);
    List<Rating> getAllRatingsOfAHotel(Long hotelId);
    Rating updateRating(Long ratingId, UpdateRatingRequestDto request);
    GetRatingDetailsResponseDto getAllDetailsOfARating(Long ratingId);
}
