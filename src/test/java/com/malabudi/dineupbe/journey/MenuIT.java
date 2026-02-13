package com.malabudi.dineupbe.journey;

import com.malabudi.dineupbe.AbstractTestcontainers;
import com.malabudi.dineupbe.common.config.JwtService;
import com.malabudi.dineupbe.common.util.Role;
import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
import com.malabudi.dineupbe.user.model.User;
import com.malabudi.dineupbe.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
class MenuIT extends AbstractTestcontainers {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTestClient restTestClient;

    private String customerToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User customer = new User(
                "Test",
                "Customer",
                "customer@test.com",
                Role.CUSTOMER,
                "testpassword123"
        );

        customerToken = jwtService.generateToken(customer);
    }

    @Test
    void customerCannotCreateMenuItem() {
        // When: Customer attempts to create menu item
        RequestMenuItemDto menuItem = new RequestMenuItemDto(
                1L,
                "Burger",
                "Tasty test burger",
                new BigDecimal("8.99"),
                null
        );

        // Then: Deny access to create menu item
        restTestClient.post()
                .uri("/api/v1/menu-items")
                .headers(h -> h.setBearerAuth(customerToken))
                .body(menuItem)
                .exchange()
                .expectStatus().isForbidden();
    }
}
