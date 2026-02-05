package com.malabudi.dineupbe.order.dto;

import java.util.List;

public record CreateOrderDto(
        List<LineItemRequestDto> items
) {}
