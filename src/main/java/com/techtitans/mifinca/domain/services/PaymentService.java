package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePaymentDTO;
import com.techtitans.mifinca.domain.entities.PaymentEntity;
import com.techtitans.mifinca.domain.entities.ScheduleEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.repository.PaymentRepository;

@Service
public class PaymentService {

    private PaymentRepository repo;
    private RentalService rentalService; 

    public PaymentService(PaymentRepository repo, RentalService rentalService){
        this.repo = repo;
        this.rentalService = rentalService;
    }
    
    private Set<String> banks = Set.of(
        "Bancolombia", "Davivienda", "Banco de Bogotá",
        "Banco de occidente", "ScotiaBank Colpatria",
        "Nequi", "NuBank", "Lula Bank"
    );

    public Set<String> getBanks(){
        return banks;
    }

    public void payRequest(UUID requestId, CreatePaymentDTO dto, AuthDTO authDTO){
        ScheduleEntity schedule = rentalService.getRentalRequestForPayment(requestId);
        if(!authDTO.userId().equals(schedule.getUser().getId())){
            throw new ApiException(ApiError.USER_IS_NOT_THE_REQUEST_ONE);
        }
        if(dto.accountNumber() == null || !banks.contains(dto.bank())){
            throw new ApiException(ApiError.INVALID_PARAMETERS);
        }
        repo.save(PaymentEntity.builder()
            .accountNumber(dto.accountNumber())
            .bank(dto.bank())
            .createdAt(LocalDateTime.now())
            .schedule(schedule)
            .value(schedule.getPrice())
            .build()
        );
        rentalService.UpdatePaidRentalRequest(requestId);
    }
    
}
