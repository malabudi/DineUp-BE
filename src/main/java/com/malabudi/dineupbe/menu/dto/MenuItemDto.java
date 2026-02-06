package com.malabudi.dineupbe.menu.dto;

import java.math.BigDecimal;

public record MenuItemDto(
        Long id,
        Long menuGroupId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {}
