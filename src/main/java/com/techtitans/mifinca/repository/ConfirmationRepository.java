package com.techtitans.mifinca.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.ConfirmationEntity;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, UUID>{
    
    public Optional<ConfirmationEntity> findByToken(@Param("token") UUID token);
}
