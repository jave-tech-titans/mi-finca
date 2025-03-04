package com.techtitans.mifinca.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.services.AccountService;

@RestController
@RequestMapping("/auth")
public class AccountController {

    @Autowired
    private AccountService service;

    @PostMapping()
    public void registerAccount(@RequestBody RegisterAccountDTO dto){
        service.registerAccount(dto);
    }
    
}
