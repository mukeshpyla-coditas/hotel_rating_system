package com.mukesh.hotel_service.repository;

import com.mukesh.hotel_service.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Running average computed INSIDE the database in one atomic statement:
     * newAvg = (oldAvg * oldCount + rating) / (oldCount + 1).
     * Doing it in SQL (not read-modify-write in Java) means two messages processed at the same time
     * cannot overwrite each other's result.
     *
     * @return number of rows updated (0 if the hotel does not exist)
     */
    @Modifying
    @Query("""
            UPDATE Hotel h
               SET h.averageRating = (COALESCE(h.averageRating, 0.0) * COALESCE(h.ratingCount, 0) + :rating)
                                     / (COALESCE(h.ratingCount, 0) + 1),
                   h.ratingCount = COALESCE(h.ratingCount, 0) + 1
             WHERE h.id = :hotelId
            """)
    int applyNewRating(@Param("hotelId") Long hotelId, @Param("rating") double rating);
}
