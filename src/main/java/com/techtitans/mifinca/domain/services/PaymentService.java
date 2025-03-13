package com.techtitans.mifinca.domain.services;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePaymentDTO;
import com.techtitans.mifinca.domain.entities.ScheduleEntity;
import com.techtitans.mifinca.repository.PaymentRepository;

@Service
public class PaymentService {

    private Set<String> banks = Set.of(
        "Banolombia", "Davivienda", "Banco de Bogotá",
        "Banco de occidente", "ScotiaBank Colpatria",
        "Nequi", "NuBank", "Lula Bank"
    );

    @Autowired
    private PaymentRepository repo;

    @Autowired
    private RentalService rentalService;  

    public Set<String> getBanks(){
        return banks;
    }

    public void payRequest(UUID requestId, CreatePaymentDTO dto, AuthDTO authDTO){
        ScheduleEntity schedule = rentalService.getRentalRequestForPayment(requestId);
    }
    
}
