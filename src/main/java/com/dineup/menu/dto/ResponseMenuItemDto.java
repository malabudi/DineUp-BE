package com.dineup.menu.dto;

import java.math.BigDecimal;

public record ResponseMenuItemDto(
        Long id,
        Long menuGroupId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {}
