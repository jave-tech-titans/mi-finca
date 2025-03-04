package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.ConfirmationEntity;
import com.techtitans.mifinca.repository.ConfirmationRepository;

@Component
public class ConfirmationService {

    @Autowired
    public ConfirmationRepository repo;
    @Autowired
    public EmailService emailService;

    public void notifyConfirmation(AccountEntity account){
        //creating the token which will be the identifier of the request
        var confirmationToken = UUID.randomUUID();
        repo.save(new ConfirmationEntity(confirmationToken, account, LocalDateTime.now()));
        
        //sending email to the users email
        emailService.sendConfirmEmail(account.getEmail(), confirmationToken.toString());
    }
    
}
