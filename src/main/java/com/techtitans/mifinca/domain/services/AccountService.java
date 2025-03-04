package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.ConfirmationEntity;
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
        if(dto.names() == null || dto.lastNames() == null || dto.email() == null || dto.password() == null ||
            dto.names().isEmpty() || dto.lastNames().isEmpty() || dto.email().isEmpty() || dto.password().isEmpty()){
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
        if(repo.findByEmail(dto.email()).orElse(null) != null){
            throw new RuntimeException("EMAIL_ALREADY_IN_USE");
        }
        //we create the entity, and then we save it (WITH ACTIVE FIELD FALSE BECAUSE IT HASNT BEEN ACTIVATED)
        //model mapper didnt work, so for now lets implement a factory method
        //AccountEntity account = modelMapper.map(dto, AccountEntity.class);
        AccountEntity account = AccountEntity.fromRegisterDTO(dto);
        account.setActive(false);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account = repo.save(account);
        confirmationService.notifyConfirmation(account);
    }

    public AccessTokenDTO confirmAccount(String token){
        UUID codeToken = UUID.fromString(token);
        //we retrieve the confirmation associated with the code
        ConfirmationEntity conf = confirmationService.getConfirmationOfCode(codeToken);
        if(conf == null){
            throw new RuntimeException("NON_EXISTING_PENDING_ACCOUNT");
        }

        Optional<AccountEntity> accountRes = repo.findById(conf.getAccount().getId());
        AccountEntity account = accountRes.orElseGet(null);
        if(account== null){
            throw new RuntimeException("NON_EXISTING_PENDING_ACCOUNT");
        }
        
        //if confirmation did exist, then update, delete confirmation, and create accesstoken
        account.setActive(true);
        account.setUpdatedAt(LocalDateTime.now());
        confirmationService.deleteConfirmation(conf.getId());
        
        return new AccessTokenDTO(createAccessToken(account.getId()));
    }

    private String createAccessToken(UUID accountId){
        ///to implemente in the futureee
        return "";
    }
}
