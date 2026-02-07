package com.malabudi.dineupbe.order.controller;

import com.malabudi.dineupbe.order.dto.CreateOrderDto;
import com.malabudi.dineupbe.order.dto.ResponseOrderDto;
import com.malabudi.dineupbe.order.dto.UpdateOrderStatusDto;
import com.malabudi.dineupbe.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ResponseOrderDto> createOrder(
            @Valid @RequestBody CreateOrderDto createOrderDto,
            Authentication authentication
    ) {
        ResponseOrderDto res = orderService.createOrder(createOrderDto, authentication.getName());
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ResponseOrderDto>> getAllOrders() {
        List<ResponseOrderDto> res = orderService.getAllOrders();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ResponseOrderDto>> getCustomerOrders(
            Authentication authentication
    ) {
        List<ResponseOrderDto> res = orderService.getCustomerOrders(authentication.getName());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseOrderDto> updateOrder(
            @Valid @RequestBody UpdateOrderStatusDto updateOrderDto
    ) {
        ResponseOrderDto res = orderService.updateOrderStatus(updateOrderDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
