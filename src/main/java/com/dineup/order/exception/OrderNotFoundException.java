package com.dineup.order.exception;

import com.dineup.common.exception.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException() {
        super("Order not found");
    }
}
