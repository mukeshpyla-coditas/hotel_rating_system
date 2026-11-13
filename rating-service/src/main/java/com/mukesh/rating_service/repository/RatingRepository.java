package com.mukesh.rating_service.repository;

import com.mukesh.rating_service.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<List<Rating>> findAllByUserId(Long userId);
    Optional<List<Rating>> findAllByHotelId(Long hotelId);
}
