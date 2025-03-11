package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.ConfirmAccountDTO;
import com.techtitans.mifinca.domain.dtos.LoginDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.ConfirmationEntity;
import com.techtitans.mifinca.repository.AccountRepository;
import com.techtitans.mifinca.utils.Helpers;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository repo;
    //@Autowired
    //private ModelMapper modelMapper;
    @Autowired
    private ConfirmationService confirmationService;
    @Autowired
    private CryptService cryptService;
        

    public void registerAccount(RegisterAccountDTO dto){
        //fields validation
        if(!Helpers.validateStrings(List.of(dto.names(), dto.lastNames(), dto.email(), dto.password()))){
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
        //storign the password but encrypted
        account.setHash(cryptService.encryptAES(dto.password()));
        account = repo.save(account);
        confirmationService.notifyConfirmation(account);
    }

    public AccessTokenDTO confirmAccount(ConfirmAccountDTO cont){
        String token = cont.code();
        UUID codeToken = UUID.fromString(token);
        //we retrieve the confirmation associated with the code
        ConfirmationEntity conf = confirmationService.getConfirmationOfCode(codeToken);
        if(conf == null){
            throw new RuntimeException("NON_EXISTING_PENDING_ACCOUNT");
        }

        AccountEntity account  = repo.findById(conf.getAccount().getId()).orElseGet(null);
        if(account== null){
            throw new RuntimeException("NON_EXISTING_PENDING_ACCOUNT");
        }
        
        //if confirmation did exist, then update, delete confirmation, and create accesstoken
        account.setActive(true);
        account.setUpdatedAt(LocalDateTime.now());
        confirmationService.deleteConfirmation(conf.getId());
        
        return new AccessTokenDTO(createAccessToken(account));
    }

    public AccessTokenDTO login(LoginDTO dto){
        AccountEntity acc = repo.findByEmail(dto.email()).orElseGet(null);
        if(acc == null || !acc.isActive()){
            throw new RuntimeException("NON_EXISTING_OR_ACTIVE_ACCOUNT");
        }
        String correctPassword = cryptService.decrypt(acc.getHash());
        if(!correctPassword.equals(dto.password())){
            throw new RuntimeException("INCORRECT_PASSWORD");
        }
        return new AccessTokenDTO(createAccessToken(acc));
    }

    private String createAccessToken(AccountEntity account){
        return "token xd";
    }


    public UUID getRandomUserUUID(){
        return repo.findAll().get(0).getId();
    }
}
