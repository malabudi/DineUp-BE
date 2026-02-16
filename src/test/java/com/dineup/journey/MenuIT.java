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

    private CreateMenuGroupDto createMenuGroupDto;

    @BeforeEach
    void setUp() {
        super.setUpTestUsers();

        createMenuGroupDto =  new CreateMenuGroupDto(
                "Main Course"
        );
    }

    @Test
    void adminCanCreateMenuItem() {
        // Create a menu group as an admin
        ResponseMenuGroupDto response = restTestClient.post()
                .uri("/api/v1/menu-groups")
                .headers(h -> h.setBearerAuth(adminToken))
                .body(createMenuGroupDto)
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
    void customerCannotCreateMenuGroup() {
        restTestClient.post()
                .uri("/api/v1/menu-groups")
                .headers(h -> h.setBearerAuth(customerToken))
                .body(createMenuGroupDto)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void customerCannotCreateMenuItem() {
        // Admin already creates a menu group first
        ResponseMenuGroupDto response = restTestClient.post()
                .uri("/api/v1/menu-groups")
                .headers(h -> h.setBearerAuth(adminToken))
                .body(createMenuGroupDto)
                .exchange()
                .expectBody(ResponseMenuGroupDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(response);

        // Customer attempts to create a menu item and gets forbidden
        RequestMenuItemDto menuItem = new RequestMenuItemDto(
                response.id(),
                "Burger",
                "Tasty test burger",
                new BigDecimal("8.99"),
                null
        );

        restTestClient.post()
                .uri("/api/v1/menu-items")
                .headers(h -> h.setBearerAuth(customerToken))
                .body(menuItem)
                .exchange()
                .expectStatus().isForbidden();
    }
}
