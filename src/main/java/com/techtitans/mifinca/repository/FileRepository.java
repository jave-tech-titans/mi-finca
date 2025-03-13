package com.techtitans.mifinca.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity,UUID>{
     @Query(value = "SELECT * FROM files WHERE property_id = :propertyId", nativeQuery = true)
    List<FileEntity> findByPropertyId(@Param("propertyId") UUID propertyId);
}
