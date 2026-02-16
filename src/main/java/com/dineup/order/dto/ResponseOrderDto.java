package com.dineup.order.dto;

import com.dineup.common.util.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ResponseOrderDto(
        Long id,
        Long customerId,
        String customerName,
        List <ResponseLineItemDto> items,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal total
) { }
