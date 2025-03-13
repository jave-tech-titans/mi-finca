package com.techtitans.mifinca.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.RatingEntity;

public interface RatingRepository extends JpaRepository<RatingEntity, UUID>{
    
    @Query(value = """
        SELECT SUM(r.rating)/COUNT(r.rating) 
        FROM ratings r
        INNER JOIN schedules s ON s.id = r.schedule_id
        WHERE s.property_id = :propertyId
        AND r.type = :landlordType
        """, nativeQuery = true)
    Double getPropertyRating(
        @Param("propertyId") UUID propertyId,
        @Param("landlordType") String landlordType
    );

}
