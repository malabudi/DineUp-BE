package com.malabudi.dineupbe.order.dto;

public record LineItemRequestDto(
        Integer menuItemId,
        Integer quantity
) {}
