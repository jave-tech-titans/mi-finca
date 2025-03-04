package com.techtitans.mifinca.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.techtitans.mifinca.domain.entities.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity,UUID>{
    public Optional<AccountEntity> findByEmail(@Param("email") String email);
}
