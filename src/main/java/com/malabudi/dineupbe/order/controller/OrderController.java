package com.malabudi.dineupbe.order.controller;

import com.malabudi.dineupbe.order.dto.CreateOrderDto;
import com.malabudi.dineupbe.order.dto.OrderDto;
import com.malabudi.dineupbe.order.model.Order;
import com.malabudi.dineupbe.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody CreateOrderDto createOrderDto,
            Authentication authentication
    ) {
        OrderDto orderDto = orderService.createOrder(createOrderDto, authentication.getName());
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }
}
