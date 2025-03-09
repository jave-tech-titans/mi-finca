package com.techtitans.mifinca.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.AccountActivationDTO;
import com.techtitans.mifinca.domain.dtos.AccountCreationDTO;
import com.techtitans.mifinca.domain.dtos.ConfirmAccountDTO;
import com.techtitans.mifinca.domain.dtos.LoginDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.services.AccountService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AccountService service;

    @PostMapping("/accounts")
    public void createAccount(@RequestBody AccountCreationDTO body) {
        AccountService.createAccount(body);
    }

    @PostMapping("/accounts/activate")
    public void activateAccount(@RequestBody AccountActivationDTO body) {
        AccountService.activateAccount(body);
    }

    @PostMapping("/sessions")
    public void login(@RequestBody LoginDTO body) {
        AccountService.login(body);
    }
    
}
