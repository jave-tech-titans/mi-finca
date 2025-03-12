package com.techtitans.mifinca.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.PropertyEntity;

public interface PropertyRepository extends JpaRepository<PropertyEntity,UUID>{
    @Query(value = """
        SELECT * FROM properties p
        WHERE (:name IS NULL OR p.name ILIKE '%' || :name || '%')
        AND (:department IS NULL OR p.department ILIKE '%' || :department || '%')
        AND (:minRooms IS NULL OR p.number_rooms >= :minRooms)
        AND (:maxRooms IS NULL OR p.number_rooms <= :maxRooms)
        AND (:minPrice IS NULL OR p.night_price >= :minPrice)
        AND (:maxPrice IS NULL OR p.night_price <= :maxPrice)
        AND (:ownerId IS NULL OR p.account_id = :ownerId)
        ORDER BY p.created_at DESC
        LIMIT :limit OFFSET :offset
        """,
        nativeQuery = true)
    List<PropertyEntity> findAllWithFilters(
        @Param("name") String name,
        @Param("department") String department,
        @Param("minRooms") Integer minRooms,
        @Param("maxRooms") Integer maxRooms,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("ownerId") UUID ownerId, 
        @Param("offset") int offset,
        @Param("limit") int limit
    );


}
