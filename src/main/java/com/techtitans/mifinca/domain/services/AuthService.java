package com.techtitans.mifinca.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.repository.AccountRepository;

@Component
public class AuthService {
    
    @Autowired
    private AccountRepository repo;

    public void registerAccount(RegisterAccountDTO dto){
        //fields validation
        if(dto.names().isEmpty() || dto.lastNames().isEmpty() || dto.email().isEmpty() || dto.password().isEmpty()){
            throw new RuntimeException("EMPTY_FIELDS");
        }
    }
}
