package com.techtitans.mifinca.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.ScheduleEntity;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, UUID>{
    
    @Query(value = """
        SELECT * FROM schedules 
        WHERE property_id = :propertyId
        AND (:year IS NULL OR EXTRACT(YEAR FROM start_date) = :year)
        AND (:month IS NULL OR EXTRACT(MONTH FROM start_date) = :month)
        LIMIT :limit
        """, nativeQuery = true)
    List<ScheduleEntity> findAllByPropertyId(
        @Param("propertyId") UUID propertyId, 
        @Param("month") Integer month, 
        @Param("year") Integer year,
        @Param("limit") Integer limit
    );
}
