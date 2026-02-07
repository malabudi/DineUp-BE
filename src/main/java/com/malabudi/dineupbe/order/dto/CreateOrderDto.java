package com.malabudi.dineupbe.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDto(
        @NotNull(message = "Order must have at least one item before being placed")
        @Valid
        List<RequestLineItemDto> items
) {}
