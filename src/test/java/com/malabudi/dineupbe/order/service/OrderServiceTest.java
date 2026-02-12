package com.malabudi.dineupbe.order.service;

import com.malabudi.dineupbe.common.util.OrderStatus;
import com.malabudi.dineupbe.common.util.Role;
import com.malabudi.dineupbe.menu.exception.MenuItemNotFoundException;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.menu.repository.MenuItemRepository;
import com.malabudi.dineupbe.order.dto.CreateOrderDto;
import com.malabudi.dineupbe.order.dto.RequestLineItemDto;
import com.malabudi.dineupbe.order.dto.ResponseOrderDto;
import com.malabudi.dineupbe.order.dto.UpdateOrderStatusDto;
import com.malabudi.dineupbe.order.exception.OrderNotFoundException;
import com.malabudi.dineupbe.order.model.LineItem;
import com.malabudi.dineupbe.order.model.Order;
import com.malabudi.dineupbe.order.repository.OrderRepository;
import com.malabudi.dineupbe.user.exception.UserNotFoundException;
import com.malabudi.dineupbe.user.model.User;
import com.malabudi.dineupbe.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private UserRepository userRepository;

    private OrderService underTest;
    private MenuItem dummyMenuItem;
    private User dummyCustomer;

    @BeforeEach
    void setUp() {
        underTest = new OrderService(orderRepository, userRepository, menuItemRepository);

        MenuGroup dummyMenuGroup = new MenuGroup("Main Course");
        dummyMenuItem = new MenuItem(
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );
        dummyMenuItem.setMenuGroup(dummyMenuGroup);

        dummyCustomer = new User(
                "John",
                "Doe",
                "johndoe@test.com",
                Role.CUSTOMER,
                "password"
        );
    }

    @Test
    void getAllOrders_shouldGetAllOrders_whenCalled() {
        // When
        underTest.getAllOrders();

        // Then
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getCustomerOrders_shouldGetCustomersOrder_whenCustomerFetches() {
        // Arrange
        Order customerOrder = new Order();
        customerOrder.setCustomer(dummyCustomer);

        LineItem lineItem =  new LineItem(customerOrder, dummyMenuItem, 5);
        customerOrder.setLineItems(List.of(lineItem));

        when(userRepository.findByEmail(dummyCustomer.getEmail())).thenReturn(Optional.of(dummyCustomer));
        when(orderRepository.findByCustomerId(dummyCustomer.getId())).thenReturn(List.of(customerOrder));

        // Act
        List<ResponseOrderDto> res = underTest.getCustomerOrders(dummyCustomer.getEmail());

        // Assert
        verify(orderRepository, times(1)).findByCustomerId(dummyCustomer.getId());
        assertThat(res.getFirst().customerName())
                .isEqualTo(dummyCustomer.getFirstName() + " " + dummyCustomer.getLastName());
        assertThat(res.getFirst().items().getFirst().menuItemName()).isEqualTo("Hamburger");
    }

    @Test
    void getCustomerOrders_shouldThrowException_whenCustomerNotFound() {
        // Arrange
        when(userRepository.findByEmail(dummyCustomer.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.getCustomerOrders(dummyCustomer.getEmail()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void createOrder_shouldCreateOrder_whenRequestSentWithValidMenuItems() {
        // Arrange
        CreateOrderDto createOrderDto = new CreateOrderDto(
                List.of(
                new RequestLineItemDto(
                        1L,
                        2
                ))
        );

        when(userRepository.findByEmail(dummyCustomer.getEmail())).thenReturn(Optional.of(dummyCustomer));
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(dummyMenuItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        underTest.createOrder(createOrderDto, dummyCustomer.getEmail());

        // Assert
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository, times(1)).save(captor.capture());
        Order capturedOrder = captor.getValue();

        assertThat(capturedOrder.getTotal()).isEqualTo(new BigDecimal("17.98"));
        assertThat(capturedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(capturedOrder.getCustomer().getFirstName()).isEqualTo(dummyCustomer.getFirstName());
        assertThat(capturedOrder.getLineItems().getFirst().getMenuItem().getName()).isEqualTo("Hamburger");
        assertThat(capturedOrder.getLineItems().getFirst().getQuantity()).isEqualTo(2);
        assertThat(capturedOrder.getLineItems().getFirst().getLineTotal()).isEqualTo(new BigDecimal("17.98"));
    }

    @Test
    void createOrder_shouldThrowException_whenCustomerNotFound() {
        // Arrange
        CreateOrderDto createOrderDto = new CreateOrderDto(
                List.of(
                        new RequestLineItemDto(
                                1L,
                                2
                        ))
        );

        when(userRepository.findByEmail(dummyCustomer.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.createOrder(createOrderDto, dummyCustomer.getEmail()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void createOrder_shouldThrowException_whenMenuItemNotFound() {
        // Arrange
        CreateOrderDto createOrderDto = new CreateOrderDto(
                List.of(
                        new RequestLineItemDto(
                                9999L,
                                2
                        ))
        );

        when(userRepository.findByEmail(dummyCustomer.getEmail())).thenReturn(Optional.of(dummyCustomer));
        when(menuItemRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.createOrder(createOrderDto, dummyCustomer.getEmail()))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessage("Menu item with id 9999 not found");
    }

    @Test
    void updateOrderStatus_shouldUpdateOrderStatus_whenNewStatusSent() {
        // Arrange
        Order customerOrder = new Order();
        Long orderId = 1L;
        customerOrder.setId(orderId);
        customerOrder.setCustomer(dummyCustomer);

        LineItem lineItem =  new LineItem(customerOrder, dummyMenuItem, 5);
        customerOrder.setLineItems(List.of(lineItem));

        when(orderRepository.findById(any())).thenReturn(Optional.of(customerOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        underTest.updateOrderStatus(new UpdateOrderStatusDto(orderId, OrderStatus.COMPLETE));

        // Assert
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository, times(1)).save(captor.capture());
        Order capturedOrder = captor.getValue();

        assertThat(capturedOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE);
    }

    @Test
    void updateOrderStatus_shouldThrowException_whenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(
                () -> underTest.updateOrderStatus(new UpdateOrderStatusDto(999L, OrderStatus.COMPLETE))
        )
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found");
    }
}
