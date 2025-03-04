package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.repository.AccountRepository;

@Component
public class AccountService {
    
    @Autowired
    private AccountRepository repo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ConfirmationService confirmationService;

    public void registerAccount(RegisterAccountDTO dto){
        //fields validation
        if(dto.names().isEmpty() || dto.lastNames().isEmpty() || dto.email().isEmpty() || dto.password().isEmpty()){
            throw new RuntimeException("EMPTY_FIELDS");
        }
        if(dto.password().length() < 8){
            throw new RuntimeException("PASSWORD_TO_SHORT");
        }
        if(!dto.email().contains("@")){
            throw new RuntimeException("INVALID_EMAIL");
        }
        var emailItems = dto.email().split("@");
        if(!emailItems[1].contains(".")){
            throw new RuntimeException("INVALID_EMAIL");
        }

        if(repo.findByEmail(dto.email()).get() != null){
            throw new RuntimeException("EMAIL_ALREADY_IN_USE");
        }

        //we create the entity, and then we save it (WITH ACTIVE FIELD FALSE BECAUSE IT HASNT BEEN ACTIVATED)
        AccountEntity account = modelMapper.map(dto, AccountEntity.class);
        account.setActive(false);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account = repo.save(account);
        confirmationService.notifyConfirmation(account);
    }
}
