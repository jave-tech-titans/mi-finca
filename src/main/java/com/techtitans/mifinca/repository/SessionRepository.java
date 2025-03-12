package com.techtitans.mifinca.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID>{

    public Optional<SessionEntity> findByToken(@Param("token") UUID token);
}
