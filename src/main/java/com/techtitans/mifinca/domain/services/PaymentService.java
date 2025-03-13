package com.techtitans.mifinca.domain.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtitans.mifinca.repository.PaymentRepository;

@Service
public class PaymentService {

    private Set<String> banks = Set.of(
        "Banolombia", "Davivienda", "Banco de Bogotá",
        "Banco de occidente", "ScotiaBank Colpatria",
        "Nequi", "NuBank", "Lula Bank"
    );

    @Autowired
    private  PaymentRepository repo;
    
}
