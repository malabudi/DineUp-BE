package com.malabudi.dineupbe.menuitem;

import java.math.BigDecimal;

public record MenuItemDto(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl
) {
}
