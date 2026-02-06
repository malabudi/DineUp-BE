package com.malabudi.dineupbe.order.dto;

public record LineItemRequestDto(
        Long menuItemId,
        Integer quantity
) {}
