package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.ConfirmationEntity;
import com.techtitans.mifinca.repository.ConfirmationRepository;


@Service
public class ConfirmationService {

    public ConfirmationRepository repo;
    public EmailService emailService;

    public ConfirmationService(
        ConfirmationRepository repo,
        EmailService emailService
    ){
        this.repo = repo;
        this.emailService = emailService;
    }

    public void notifyConfirmation(AccountEntity account){
        //creating the token which will be the identifier of the request
        var confirmationToken = UUID.randomUUID();
        repo.save(new ConfirmationEntity(confirmationToken, account, LocalDateTime.now()));
        
        //sending email to the users email
        emailService.sendConfirmEmail(account.getEmail(), confirmationToken.toString());
    }
    

    public ConfirmationEntity getConfirmationOfCode(UUID code){
        var response = repo.findByToken(code);
        return response.orElse(null);
    }

    public void deleteConfirmation(UUID confirmationId){
        repo.deleteById(confirmationId);
    }
}
