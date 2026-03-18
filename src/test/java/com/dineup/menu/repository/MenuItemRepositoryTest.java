package com.dineup.menu;

import com.dineup.AbstractTestcontainers;
import com.dineup.common.util.OrderStatus;
import com.dineup.common.util.Role;
import com.dineup.menu.dto.MenuItemSalesDto;
import com.dineup.menu.model.MenuGroup;
import com.dineup.menu.model.MenuItem;
import com.dineup.menu.repository.MenuItemRepository;
import com.dineup.order.model.LineItem;
import com.dineup.order.model.Order;
import com.dineup.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MenuItemRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private MenuItemRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    private MenuItem burger;
    private MenuItem drink;
    private User customer;

    @BeforeEach
    void setup() {
        MenuGroup burgerGroup = new MenuGroup("Burgers");
        entityManager.persistAndFlush(burgerGroup);

        MenuGroup drinkGroup = new MenuGroup("Burgers");
        entityManager.persistAndFlush(drinkGroup);

        burger = new MenuItem("Classic Burger", "Desc", new BigDecimal("9.99"), "");
        burger.setMenuGroup(burgerGroup);
        drink = new MenuItem("Lemonade", "Desc", new BigDecimal("3.99"), "");
        drink.setMenuGroup(drinkGroup);
        entityManager.persistAndFlush(burger);
        entityManager.persistAndFlush(drink);

        customer = new User("John", "Doe", "john@test.com", Role.CUSTOMER, "password");
        entityManager.persistAndFlush(customer);
    }

    @Test
    void findBestSellingMenuItems_ShouldReturnItemsOrderedByQuantity() {
        // Arrange
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderStatus(OrderStatus.COMPLETE);
        entityManager.persistAndFlush(order);

        LineItem lineItem1 = new LineItem(order, burger, 5);
        LineItem lineItem2 = new LineItem(order, drink, 2);
        entityManager.persistAndFlush(lineItem1);
        entityManager.persistAndFlush(lineItem2);

        // Act
        List<MenuItemSalesDto> result = underTest.findBestSellingMenuItems(PageRequest.of(0, 10));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).menuItemName()).isEqualTo("Classic Burger");
        assertThat(result.get(0).totalQuantitySold()).isEqualTo(5L);
        assertThat(result.get(1).menuItemName()).isEqualTo("Lemonade");
        assertThat(result.get(1).totalQuantitySold()).isEqualTo(2L);
    }

    @Test
    void findBestSellingMenuItems_ShouldExcludeNonCompleteOrders() {
        // Arrange
        Order pendingOrder = new Order();
        pendingOrder.setCustomer(customer);
        entityManager.persistAndFlush(pendingOrder);

        LineItem lineItem = new LineItem(pendingOrder, burger, 10);
        entityManager.persistAndFlush(lineItem);

        // Act
        List<MenuItemSalesDto> result = underTest.findBestSellingMenuItems(PageRequest.of(0, 10));

        // Assert
        assertThat(result).isEmpty();
    }
}