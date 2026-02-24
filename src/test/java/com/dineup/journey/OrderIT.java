package com.dineup.journey;

import com.dineup.BaseIT;
import com.dineup.menu.repository.MenuGroupRepository;
import com.dineup.menu.repository.MenuItemRepository;
import com.dineup.order.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
class OrderIT extends BaseIT {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        super.setUpTestUsers();
    }

    @AfterEach
    void tearDown() {
        menuItemRepository.deleteAll();
        menuGroupRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void customerCanPlaceOrder() {

    }
}
