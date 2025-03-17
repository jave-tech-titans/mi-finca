package com.techtitans.mifinca.transport.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.ConfirmAccountDTO;
import com.techtitans.mifinca.domain.dtos.LoginDTO;
import com.techtitans.mifinca.domain.dtos.RefreshTokenDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.services.AccountService;
import com.techtitans.mifinca.domain.services.AuthService;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AccountService service;
    private AuthService authService;

    public AuthController(AccountService service, AuthService authService){
        this.service = service;
        this.authService = authService;
    }

    //FOR BOTH TYPE OF USERS
    @PostMapping("/accounts")
    public void createAccount(@RequestBody RegisterAccountDTO body) {
        service.registerAccount(body);
    }

    @PostMapping("/accounts/activate")
    public AccessTokenDTO activateAccount(@RequestBody ConfirmAccountDTO body) {
        return service.confirmAccount(body);
    }

    @PostMapping("/sessions")
    public AccessTokenDTO login(@RequestBody LoginDTO body) {
        return service.login(body);
    }

    @PostMapping("/refresh")
    public AccessTokenDTO refresh(@RequestBody RefreshTokenDTO body){
        return authService.refreshAccessToken(body.refreshToken());
    }
    
}
