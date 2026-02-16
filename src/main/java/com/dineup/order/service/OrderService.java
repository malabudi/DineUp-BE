package com.dineup.order.service;

import com.dineup.menu.exception.MenuItemNotFoundException;
import com.dineup.menu.model.MenuItem;
import com.dineup.menu.repository.MenuItemRepository;
import com.dineup.order.dto.CreateOrderDto;
import com.dineup.order.dto.RequestLineItemDto;
import com.dineup.order.dto.ResponseOrderDto;
import com.dineup.order.dto.UpdateOrderStatusDto;
import com.dineup.order.exception.OrderNotFoundException;
import com.dineup.order.mapper.OrderMapper;
import com.dineup.order.model.LineItem;
import com.dineup.order.model.Order;
import com.dineup.order.repository.OrderRepository;
import com.dineup.user.exception.UserNotFoundException;
import com.dineup.user.model.User;
import com.dineup.user.repository.UserRepository;
import org.springframework.stereotype.Service;

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
