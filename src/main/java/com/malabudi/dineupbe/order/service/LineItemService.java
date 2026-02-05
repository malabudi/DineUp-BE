package com.malabudi.dineupbe.order.service;

import com.malabudi.dineupbe.order.repository.LineItemRepository;
import org.springframework.stereotype.Service;

@Service
public class LineItemService {
    private final LineItemRepository lineItemRepository;

    public LineItemService(LineItemRepository lineItemRepository) {
        this.lineItemRepository = lineItemRepository;
    }
}
