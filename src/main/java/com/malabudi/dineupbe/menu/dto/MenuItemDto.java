package com.malabudi.dineupbe.menu.dto;

import java.math.BigDecimal;

public record MenuItemDto(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {}
