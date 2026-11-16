package com.mukesh.rating_service.service.impl;

import com.mukesh.rating_service.client.HotelServiceClient;
import com.mukesh.rating_service.client.UserServiceClient;
import com.mukesh.rating_service.dto.request.CreateRatingRequestDto;
import com.mukesh.rating_service.dto.request.UpdateRatingRequestDto;
import com.mukesh.rating_service.dto.response.*;
import com.mukesh.rating_service.entity.Rating;
import com.mukesh.rating_service.exception.ResourceNotFoundException;
import com.mukesh.rating_service.payload.ApiResponse;
import com.mukesh.rating_service.repository.RatingRepository;
import com.mukesh.rating_service.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final UserServiceClient userServiceClient;
    private final HotelServiceClient hotelServiceClient;

    @Override
    public String createRating(CreateRatingRequestDto request) {
        // Step-1: Validate the provided userId
        userServiceClient.getUserById(request.userId());
        log.info("User ID-{} is validated!", request.userId());

        // Step-2: Validate the provided hotelId
        hotelServiceClient.getHotelById(request.hotelId());
        log.info("Hotel ID-{} is validated!", request.hotelId());

        // Step-3: Build the rating with verified userId and hotelId
        Rating newRating = Rating.builder()
                .rating(request.rating())
                .userId(request.userId())
                .hotelId(request.hotelId())
                .feedback(request.feedback())
                .build();
        ratingRepository.save(newRating);
        log.info("New rating is created by User-{} on Hotel-{}", request.userId(), request.hotelId());

        return "Rating by user-" + request.userId() + " is successfully registered on the hotel-" + request.hotelId() + " !";
    }

    @Override
    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rating with specified ID is not found!"));
    }

    @Override
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    @Override
    public List<Rating> getAllRatingsSubmittedByUser(Long userId) {
        return ratingRepository.findAllByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("There are no ratings found for the specified userID."));
    }

    @Override
    public List<Rating> getAllRatingsOfAHotel(Long hotelId) {
        return ratingRepository.findAllByHotelId(hotelId).orElseThrow(() -> new ResourceNotFoundException("There are no ratings found for the specified hotelID."));
    }

    @Override
    public Rating updateRating(Long ratingId, UpdateRatingRequestDto request) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rating not found with ID: " + ratingId
                ));

        if (request.rating() != null) {
            rating.setRating(request.rating());
        }

        if (request.feedback() != null) {
            rating.setFeedback(request.feedback());
        }

        log.info("Updated the details of the specified rating-{}", rating.getId());
        return ratingRepository.save(rating);
    }

    @Override
    public GetRatingDetailsResponseDto getAllDetailsOfARating(Long ratingId) {
        Rating requestedRating = ratingRepository.findById(ratingId).orElseThrow(() -> new ResourceNotFoundException("No rating is found with specified ID. Please verify and try again."));
        // Make a call to UserService to get the details of the user
        ApiResponse<UserServiceResponseDto> userServiceResponse = userServiceClient.getUserById(requestedRating.getUserId());
        UserServiceResponseDto user = userServiceResponse.data();
        log.info("Details of the user-{with user ID-{}} are fetched successfully!", requestedRating.getUserId());
        // Make a call to HotelService to get the details of the hotel
        ApiResponse<HotelServiceResponseDto> hotelServiceResponse = hotelServiceClient.getHotelById(requestedRating.getHotelId());
        HotelServiceResponseDto hotel = hotelServiceResponse.data();
        log.info("Details of the hotel-{with user ID-{}} are fetched successfully!", requestedRating.getHotelId());

        UserDetailsResponseDto userDetails = UserDetailsResponseDto.builder()
                .userName(user.firstName() + " " + user.middleName() + " " + user.lastName())
                .contactNumber(user.contactNumber())
                .email(user.email())
                .build();

        HotelDetailsResponseDto hotelDetails = HotelDetailsResponseDto.builder()
                .hotelName(hotel.name())
                .location(hotel.location())
                .contactEmail(hotel.email())
                .build();

        return GetRatingDetailsResponseDto.builder()
                .feedback(requestedRating.getFeedback())
                .ratingId(requestedRating.getId())
                .rating(requestedRating.getRating())
                .hotelDetails(hotelDetails)
                .userDetails(userDetails)
                .build();
    }


}
