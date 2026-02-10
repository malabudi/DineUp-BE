package com.malabudi.dineupbe.order.repository;

import com.malabudi.dineupbe.AbstractTestcontainers;
import com.malabudi.dineupbe.common.util.Role;
import com.malabudi.dineupbe.menu.model.MenuGroup;
import com.malabudi.dineupbe.menu.model.MenuItem;
import com.malabudi.dineupbe.order.model.LineItem;
import com.malabudi.dineupbe.order.model.Order;
import com.malabudi.dineupbe.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class OrderRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private OrderRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    private User dummyCustomer1;
    private User dummyCustomer2;
    private MenuItem dummyMenuItem;

    @BeforeEach
    void setup() {
        underTest.deleteAll();

        dummyCustomer1 = new User(
                "John",
                "Doe",
                "johndoe@test.com",
                Role.CUSTOMER,
                "password"
        );
        entityManager.persist(dummyCustomer1);

        dummyCustomer2 = new User(
                "Jane",
                "Doe",
                "janedoe@test.com",
                Role.CUSTOMER,
                "password"
        );
        entityManager.persist(dummyCustomer2);

        MenuGroup dummyMenuGroup = new MenuGroup("Main Course");
        entityManager.persist(dummyMenuGroup);

        dummyMenuItem = new MenuItem(
                "Hamburger",
                "Tasty burger",
                new BigDecimal("8.99"),
                null
        );
        dummyMenuItem.setMenuGroup(dummyMenuGroup);
        entityManager.persist(dummyMenuItem);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByCustomerId_shouldReturnOneOrder_whenCustomerHasOneOrder() {
        // Arrange
        Order order = new Order();
        order.setCustomer(dummyCustomer1);
        LineItem lineItem = new LineItem(order, dummyMenuItem, 2);
        order.getLineItems().add(lineItem);
        order.calculateTotalPrice();
        entityManager.persist(order);

        // Act
        List<Order> orders = underTest.findByCustomerId(dummyCustomer1.getId());

        // Assert
        assertThat(orders).isNotEmpty();
        assertThat(orders).hasSize(1);
        assertThat(orders.getFirst().getCustomer().getId()).isEqualTo(dummyCustomer1.getId());
        assertThat(orders.getFirst().getTotal()).isEqualTo(new BigDecimal("17.98"));
        assertThat(orders.getFirst().getLineItems()).hasSize(1);
    }

    @Test
    void findByCustomerId_shouldReturnEmptyList_whenCustomerHasNoOrders() {
        // Arrange - None

        // Act
        List<Order> orders = underTest.findByCustomerId(dummyCustomer1.getId());

        // Assert
        assertThat(orders).isEmpty();
    }

    @Test
    void findByCustomerId_shouldReturnMultipleOrders_whenCustomerHasMultipleOrders() {
        // Arrange
        Order order1 = new Order();
        order1.setCustomer(dummyCustomer1);
        LineItem lineItem1 = new LineItem(order1, dummyMenuItem, 2);
        order1.getLineItems().add(lineItem1);
        order1.calculateTotalPrice();
        entityManager.persist(order1);

        Order order2 = new Order();
        order2.setCustomer(dummyCustomer1);
        LineItem lineItem2 = new LineItem(order2, dummyMenuItem, 5);
        order2.getLineItems().add(lineItem2);
        order2.calculateTotalPrice();
        entityManager.persist(order2);

        // Act
        List<Order> orders = underTest.findByCustomerId(dummyCustomer1.getId());

        // Assert
        assertThat(orders).hasSize(2);
        assertThat(orders.getFirst().getCustomer().getId()).isEqualTo(dummyCustomer1.getId());
        assertThat(orders.get(1).getCustomer().getId()).isEqualTo(dummyCustomer1.getId());
        assertThat(orders.getFirst().getTotal()).isEqualTo(new BigDecimal("17.98"));
        assertThat(orders.get(1).getTotal()).isEqualTo(new BigDecimal("44.95"));
    }

    @Test
    void findByCustomerId_shouldNotReturnOtherCustomersOrders_whenOtherCustomerHasOrders() {
        // Arrange
        Order order = new Order();
        order.setCustomer(dummyCustomer1);
        entityManager.persist(order);

        // Act
        List<Order> orders = underTest.findByCustomerId(dummyCustomer1.getId());

        // Assert
        assertThat(orders).hasSize(1);
        assertThat(orders)
                .noneMatch(o -> o.getCustomer().getId().equals(dummyCustomer2.getId()));
    }
}
