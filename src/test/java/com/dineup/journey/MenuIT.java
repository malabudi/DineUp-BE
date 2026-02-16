package com.dineup.journey;

import com.dineup.BaseIT;
import com.dineup.menu.dto.CreateMenuGroupDto;
import com.dineup.menu.dto.RequestMenuItemDto;
import com.dineup.menu.dto.ResponseMenuGroupDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
class MenuIT extends BaseIT {

    @BeforeEach
    void setUp() {
        super.setUpTestUsers();
    }

    @Test
    void adminCanCreateMenuItem() {
        // Create a menu group as an admin
        CreateMenuGroupDto menuGroup = new CreateMenuGroupDto(
                "Main Course"
        );

        var response = restTestClient.post()
                .uri("/api/v1/menu-groups")
                .headers(h -> h.setBearerAuth(adminToken))
                .body(menuGroup)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuGroupDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);

        RequestMenuItemDto menuItem = new RequestMenuItemDto(
                response.id(),
                "Burger",
                "Tasty test burger",
                new BigDecimal("8.99"),
                null
        );

        // Create a menu item
        restTestClient.post()
                .uri("/api/v1/menu-items")
                .headers(h -> h.setBearerAuth(adminToken))
                .body(menuItem)
                .exchange()
                .expectStatus().isCreated();

        // Fetch the menu item to verify the item was saved
        restTestClient.get()
                .uri("/api/v1/menu-items")
                .headers(h -> h.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].name").isEqualTo("Burger");
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
