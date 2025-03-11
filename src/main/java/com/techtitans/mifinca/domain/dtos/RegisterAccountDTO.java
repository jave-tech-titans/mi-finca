package com.techtitans.mifinca.domain.dtos;

public record RegisterAccountDTO(
    String names,
    String lastNames,
    String email,
    String password,
    String number,
    String role
){}