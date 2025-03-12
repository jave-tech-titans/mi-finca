package com.techtitans.mifinca.domain.dtos;

import java.util.UUID;

public record AuthDTO (UUID userId, String role) {}
