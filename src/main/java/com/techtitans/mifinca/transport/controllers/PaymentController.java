package com.techtitans.mifinca.transport.controllers;

import java.util.Set;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePaymentDTO;
import com.techtitans.mifinca.domain.services.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private PaymentService service; 

    public PaymentController(PaymentService service){
        this.service = service;
    }

    @GetMapping("/banks")
    public Set<String> getBanks(){
        return service.getBanks(); 
    }   

    @PostMapping("/requests/{request-id}/payments")
    public void paySchedule(
        @PathVariable("request-id") UUID requestId,  
        @RequestBody CreatePaymentDTO dto,
        @RequestAttribute("auth") AuthDTO authDTO     
    ){
        service.payRequest(requestId,dto, authDTO);
    }
}
