package com.malabudi.dineupbe.order.exception;

import com.malabudi.dineupbe.common.exception.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException() {
        super("Order not found");
    }
}
