package com.malabudi.dineupbe.order.service;

import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import com.malabudi.dineupbe.order.dto.CreateOrderDto;
import com.malabudi.dineupbe.order.dto.LineItemRequestDto;
import com.malabudi.dineupbe.order.dto.OrderDto;
import com.malabudi.dineupbe.order.mapper.OrderMapper;
import com.malabudi.dineupbe.order.model.LineItem;
import com.malabudi.dineupbe.order.model.Order;
import com.malabudi.dineupbe.order.repository.OrderRepository;
import com.malabudi.dineupbe.user.model.User;
import com.malabudi.dineupbe.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public OrderDto createOrder(CreateOrderDto createOrderDto, String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        Order order = new Order();
        order.setCustomer(customer);

        for (LineItemRequestDto lineItemDto : createOrderDto.items()) {
            MenuItem menuItem = menuItemRepository.findById(lineItemDto.menuItemId())
                    .orElseThrow(()-> new MenuItemNotFoundException("Menu item not found"));

            order.getLineItems().add(new LineItem(order, menuItem, lineItemDto.quantity()));
        }

        order.calculateTotalPrice();

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderDto(savedOrder);
    }
}
