package com.malabudi.dineupbe.order.dto;

import com.malabudi.dineupbe.common.util.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        String customerName,
        List <LineItemDto> items,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal total
) { }
