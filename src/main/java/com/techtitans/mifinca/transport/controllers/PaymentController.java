package com.techtitans.mifinca.transport.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private PaymentService service; 

    @GetMapping("/banks")
    public Set<String> getBanks(){
        return service.getBanks(); 
    }   

    @PostMapping("/requests/{request-id}/payments")
    public void paySchedule(
        @RequestBody CreatePaymentDTO dto,
        @RequestAttribute("auth") AuthDTO authDTO     
    ){
        service.payRequest(dto, authDTO);
    }
}
