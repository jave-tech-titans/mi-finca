package com.techtitans.mifinca.domain.dtos;

public record CreatePaymentDTO(
    String bank,
    Long accountNumber
){}
