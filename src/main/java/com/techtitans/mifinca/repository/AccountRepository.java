package com.techtitans.mifinca.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techtitans.mifinca.domain.entities.AccountEntity;

public interface AccountRepository extends JpaRepository<UUID, AccountEntity>{
    
}
