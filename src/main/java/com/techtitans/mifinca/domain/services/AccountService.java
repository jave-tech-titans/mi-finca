package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.ConfirmAccountDTO;
import com.techtitans.mifinca.domain.dtos.LoginDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.ConfirmationEntity;
import com.techtitans.mifinca.domain.entities.Roles;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.repository.AccountRepository;
import com.techtitans.mifinca.utils.Helpers;


@Service
public class AccountService {

    private AccountRepository repo;
    private ConfirmationService confirmationService;
    private CryptService cryptService;
    private AuthService authService;

    public AccountService(
        AccountRepository repo,
        ConfirmationService confirmationService,
        CryptService cryptService,
        AuthService authService
    ){
        this.repo = repo;
        this.confirmationService = confirmationService;
        this.cryptService = cryptService;
        this.authService = authService;
    }
    
    //method which registers the new account, account creation
    public void registerAccount(RegisterAccountDTO dto){
        //fields validation
        if(!Helpers.validateStrings(List.of(
            dto.names() == null ? "" :  dto.names(), 
            dto.lastNames()== null ? "" :  dto.lastNames(), 
            dto.email()== null ? "" :  dto.email(), 
            dto.password() == null ? "" :  dto.password()
        ))){
            throw new ApiException(ApiError.EMPTY_FIELDS);
        }
        if(dto.password().length() < 8){
            throw new ApiException(ApiError.PASSWORD_TO_SHORT);
        }
        if(!dto.email().contains("@")){
            throw new ApiException(ApiError.INVALID_EMAIL);
        }
        var emailItems = dto.email().split("@");
        if(!emailItems[1].contains(".")){
            throw new ApiException(ApiError.INVALID_EMAIL);
        }
        if(repo.findByEmail(dto.email()).orElse(null) != null){
            throw new ApiException(ApiError.EMAIL_ALREADY_TAKEN);
        }
        //we create the entity, and then we save it (WITH ACTIVE FIELD FALSE BECAUSE IT HASNT BEEN ACTIVATED)
        //model mapper didnt work, so for now lets implement a factory method
        AccountEntity account = AccountEntity.fromRegisterDTO(dto);
        //setting the role depending on what asked
        if(dto.role().toUpperCase().trim().equals("LANDLORD")){
            account.setRole(Roles.LANDLORD_ROLE);
        }else{
            account.setRole(Roles.USER_ROLE);
        }
        account.setActive(false);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        //storign the password but encrypted
        account.setHash(cryptService.encryptAES(dto.password()));
        account = repo.save(account);
        confirmationService.notifyConfirmation(account);
    }


    //method used when the user has received the confirmation button, that button will
    //make a call to the endpoint with a unique code token, with that the account is confirmed
    public AccessTokenDTO confirmAccount(ConfirmAccountDTO cont){
        String token = cont.code();
        UUID codeToken = UUID.fromString(token);
        //we retrieve the confirmation associated with the code
        ConfirmationEntity conf = confirmationService.getConfirmationOfCode(codeToken);
        if(conf == null){
            throw new ApiException(ApiError.NON_EXISTING_ACCOUNT);
        }

        AccountEntity account  = repo.findById(conf.getAccount().getId()).orElse(null);
        if(account== null){
            throw new ApiException(ApiError.NON_EXISTING_ACCOUNT);
        }
        
        //if confirmation did exist, then update, delete confirmation, and create accesstoken
        account.setActive(true);
        account.setUpdatedAt(LocalDateTime.now());
        confirmationService.deleteConfirmation(conf.getId());

        //create access and refresh tokens, with auth service
        return authService.createSession(account);
    }


    //method for the user to login into the account
    public AccessTokenDTO login(LoginDTO dto){
        AccountEntity acc = repo.findByEmail(dto.email()).orElse(null);
        if(acc == null || !acc.isActive()){
            throw new ApiException(ApiError.NON_EXISTING_ACCOUNT);
        }
        String correctPassword = cryptService.decrypt(acc.getHash());
        if(!correctPassword.equals(dto.password())){
            throw new ApiException(ApiError.INCORRECT_PASSWORD);
        }
        //if credentials were correct, then we create the session
        return authService.createSession(acc);
    }

}
