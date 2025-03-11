package com.techtitans.mifinca.domain.dtos;

public record AccessTokenDTO (
    String accessToken,
    String refreshToken
){   }
