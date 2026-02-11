package com.malabudi.dineupbe.order.service;

import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import com.malabudi.dineupbe.order.dto.CreateOrderDto;
import com.malabudi.dineupbe.order.dto.RequestLineItemDto;
import com.malabudi.dineupbe.order.dto.ResponseOrderDto;
import com.malabudi.dineupbe.order.dto.UpdateOrderStatusDto;
import com.malabudi.dineupbe.order.exception.OrderNotFoundException;
import com.malabudi.dineupbe.order.mapper.OrderMapper;
import com.malabudi.dineupbe.order.model.LineItem;
import com.malabudi.dineupbe.order.model.Order;
import com.malabudi.dineupbe.order.repository.OrderRepository;
import com.malabudi.dineupbe.user.exception.UserNotFoundException;
import com.malabudi.dineupbe.user.model.User;
import com.malabudi.dineupbe.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

    public ResponseOrderDto createOrder(CreateOrderDto createOrderDto, String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        Order order = new Order();
        order.setCustomer(customer);

        for (RequestLineItemDto lineItemDto : createOrderDto.items()) {
            MenuItem menuItem = menuItemRepository.findById(lineItemDto.menuItemId())
                    .orElseThrow(() -> new MenuItemNotFoundException(lineItemDto.menuItemId()));

            order.getLineItems().add(new LineItem(order, menuItem, lineItemDto.quantity()));
        }

        order.calculateTotalPrice();

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toDto(savedOrder);
    }

    public List<ResponseOrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    public List<ResponseOrderDto> getCustomerOrders(String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        return orders.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    public ResponseOrderDto updateOrderStatus(UpdateOrderStatusDto updateOrderDto) {
        Order order = orderRepository.findById(updateOrderDto.orderId())
                .orElseThrow(OrderNotFoundException::new);

        order.setOrderStatus(updateOrderDto.orderStatus());
        Order updatedOrder = orderRepository.save(order);

        return OrderMapper.toDto(updatedOrder);
    }
}
