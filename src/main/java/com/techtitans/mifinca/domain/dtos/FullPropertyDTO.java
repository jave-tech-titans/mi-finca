package com.techtitans.mifinca.domain.dtos;

import java.util.List;
import java.util.UUID;

public record FullPropertyDTO (
    UUID id,
    String name,
    String department,
    String description,
    String enterType,
    boolean hasAsador,
    boolean hasPool,
    boolean isPetFriendly,
    int numberBathrooms,
    int numberRooms,
    double nightPrice,
    UUID ownerId,
    Double rating,
    List<String> pictures
){}
