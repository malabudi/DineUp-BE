package com.malabudi.dineupbe.order.dto;

import java.math.BigDecimal;

public record LineItemDto(
        Long id,
        Long orderId,
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal price,
        BigDecimal lineTotal
) {}
