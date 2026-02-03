package com.malabudi.dineupbe.menu.dto;

import java.math.BigDecimal;

public record MenuItemDto(
        Integer id,
        Integer menuGroupId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {}
