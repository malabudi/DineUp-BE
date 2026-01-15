package com.malabudi.dineupbe.dto;

import java.math.BigDecimal;

public record MenuItemDto(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {
}
