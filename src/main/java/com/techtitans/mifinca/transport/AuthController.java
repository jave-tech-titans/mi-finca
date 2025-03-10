package com.techtitans.mifinca.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.ConfirmAccountDTO;
import com.techtitans.mifinca.domain.dtos.LoginDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.services.AccountService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountService service;

    @PostMapping()
    public void registerAccount(@RequestBody RegisterAccountDTO dto){
        service.registerAccount(dto);
    }

    @PostMapping("/confirm")
    public AccessTokenDTO registerAccount(@RequestBody ConfirmAccountDTO body){
        return service.confirmAccount(body.code());
    }  

   @PostMapping("/login")
   public AccessTokenDTO login(@RequestBody LoginDTO body){
    return service.login(body);
   } 
    
}
