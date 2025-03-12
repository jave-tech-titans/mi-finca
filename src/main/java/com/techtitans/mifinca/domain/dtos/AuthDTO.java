package com.techtitans.mifinca.domain.dtos;

import java.util.UUID;

import com.techtitans.mifinca.domain.entities.Role;

public record AuthDTO (UUID userId, Role role) {}
