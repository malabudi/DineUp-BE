package com.dineup.journey;

import com.dineup.BaseIT;
import com.dineup.common.dto.ErrorResponseDto;
import com.dineup.menu.dto.*;
import com.dineup.menu.repository.MenuGroupRepository;
import com.dineup.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
class MenuIT extends BaseIT {
    private static final String MENU_GROUP_URI = "/api/v1/menu-groups";
    private static final String MENU_ITEM_URI = "/api/v1/menu-items";
    private static final String TEST_ITEM_NAME = "Burger";
    private static final String TEST_ITEM_DESCRIPTION = "Tasty test burger";
    private static final BigDecimal TEST_ITEM_PRICE = new BigDecimal("8.99");
    private static final String TEST_MENU_GROUP_NAME = "Main Course";

    private CreateMenuGroupDto createMenuGroupDto;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        super.setUpTestUsers();

        createMenuGroupDto =  new CreateMenuGroupDto(
                TEST_MENU_GROUP_NAME
        );
    }

    @AfterEach
    void tearDown() {
        menuItemRepository.deleteAll();
        menuGroupRepository.deleteAll();
    }

    // Helpers
    private RequestMenuItemDto buildMenuItemDto(Long menuGroupId) {
        return new RequestMenuItemDto(
                menuGroupId,
                TEST_ITEM_NAME,
                TEST_ITEM_DESCRIPTION,
                TEST_ITEM_PRICE,
                null
        );
    }

    private ResponseMenuGroupDto createMenuGroup(String token) {
        ResponseMenuGroupDto menuGroup = restTestClient.post()
                .uri(MENU_GROUP_URI)
                .headers(h -> h.setBearerAuth(token))
                .body(createMenuGroupDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(menuGroup).isNotNull();
        return menuGroup;
    }

    private ResponseMenuItemDto createMenuItem(ResponseMenuGroupDto menuGroup, String token) {
        RequestMenuItemDto reqMenuItem = buildMenuItemDto(menuGroup.id());

        ResponseMenuItemDto menuItem = restTestClient.post()
                .uri(MENU_ITEM_URI)
                .headers(h -> h.setBearerAuth(token))
                .body(reqMenuItem)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuItemDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(menuItem).isNotNull();
        return menuItem;
    }


    @Test
    void adminCanCreateMenuItem() {
        // Create a menu group and item as an admin
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);

        restTestClient.post()
                .uri(MENU_ITEM_URI)
                .headers(h -> h.setBearerAuth(adminToken))
                .body(buildMenuItemDto(menuGroup.id()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuItemDto.class)
                .value(menuItemRes -> {
                    assertThat(menuItemRes).isNotNull();
                    assertThat(menuItemRes.menuGroupId()).isEqualTo(menuGroup.id());
                    assertThat(menuItemRes.name()).isEqualTo("Burger");
                    assertThat(menuItemRes.description()).isEqualTo("Tasty test burger");
                    assertThat(menuItemRes.price()).isEqualTo(new BigDecimal("8.99"));
                });
    }

    @Test
    void adminCanCreateMenuGroup() {
        // Create a menu group as an admin
        restTestClient.post()
                .uri(MENU_GROUP_URI)
                .headers(h -> h.setBearerAuth(adminToken))
                .body(createMenuGroupDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuGroupDto.class)
                .value( menuGroupRes -> {
                    assertThat(menuGroupRes).isNotNull();
                    assertThat(menuGroupRes.name()).isEqualTo("Main Course");
                });
    }

    @Test
    void customerCannotCreateMenuGroup() {
        restTestClient.post()
                .uri(MENU_GROUP_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(createMenuGroupDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Forbidden");
                    assertThat(error.errorMessage()).isEqualTo("Access Denied");
                });
    }

    @Test
    void customerCannotCreateMenuItem() {
        // Customer attempts to create a menu item and gets forbidden
        RequestMenuItemDto menuItem = buildMenuItemDto(1L);

        restTestClient.post()
                .uri(MENU_ITEM_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(menuItem)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Forbidden");
                    assertThat(error.errorMessage()).isEqualTo("Access Denied");
                });
    }

    @Test
    void anyUserCanGetMenuItems() {
        // Assume a menu item is already created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);
        createMenuItem(menuGroup, adminToken);

        // Fetch all menu items
        restTestClient.get()
                .uri(MENU_ITEM_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].menuGroupId").isEqualTo(menuGroup.id())
                .jsonPath("$.[0].name").isEqualTo(TEST_ITEM_NAME)
                .jsonPath("$.[0].description").isEqualTo(TEST_ITEM_DESCRIPTION)
                .jsonPath("$.[0].price").isEqualTo(TEST_ITEM_PRICE);
    }

    @Test
    void anyUserCanGetMenuGroups() {
        // Assume a menu group is already created
        createMenuGroup(adminToken);

        // Fetch all menu groups
        restTestClient.get()
                .uri(MENU_GROUP_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].name").isEqualTo(TEST_MENU_GROUP_NAME);
    }

    @Test
    void anyUserCanGetMenuGroupById() {
        // When a menu group is created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);

        // Then fetch it by id
        restTestClient.get()
                .uri(MENU_GROUP_URI + "/" + menuGroup.id())
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMenuGroupDto.class)
                .value(menuGroupRes -> {
                    assertThat(menuGroupRes).isNotNull();
                    assertThat(menuGroupRes.name()).isEqualTo(TEST_MENU_GROUP_NAME);
                });
    }

    @Test
    void anyUserCanGetMenuItemById() {
        // When a menu group is created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);
        ResponseMenuItemDto menuItem = createMenuItem(menuGroup, adminToken);

        // Then fetch it by id
        restTestClient.get()
                .uri(MENU_ITEM_URI + "/" + menuItem.id())
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMenuItemDto.class)
                .value(menuItemRes -> {
                    assertThat(menuItemRes).isNotNull();
                    assertThat(menuItemRes.name()).isEqualTo(TEST_ITEM_NAME);
                    assertThat(menuItemRes.description()).isEqualTo(TEST_ITEM_DESCRIPTION);
                    assertThat(menuItemRes.price()).isEqualTo(TEST_ITEM_PRICE);
                });
    }

    @Test
    void shouldThrowExceptionWhenMenuGroupNotFound() {
        long id = 9999999L;

        // Throw an exception when a menu group is fetched and not found
        restTestClient.get()
                .uri(MENU_GROUP_URI + "/" + id)
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Not Found");
                    assertThat(error.errorMessage()).isEqualTo("Menu group with id " + id + " not found");
                });
    }

    @Test
    void shouldThrowExceptionWhenMenuItemNotFound() {
        long id = 9999999L;

        // Throw an exception when a menu item is fetched and not found
        restTestClient.get()
                .uri(MENU_ITEM_URI + "/" + id)
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Not Found");
                    assertThat(error.errorMessage()).isEqualTo("Menu item with id " + id + " not found");
                });
    }

    @Test
    void adminCanUpdateMenuGroup() {
        // When a menu group exists
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);

        // Update the name of the menu group
        String newMenuGroupName = "Appetizers";

        restTestClient.patch()
                .uri(MENU_GROUP_URI + "/" + menuGroup.id())
                .headers(h -> h.setBearerAuth(adminToken))
                .body(new UpdateMenuGroupDto(newMenuGroupName))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMenuGroupDto.class)
                .value(menuGroupRes -> {
                    assertThat(menuGroupRes).isNotNull();
                    assertThat(menuGroupRes.name()).isEqualTo(newMenuGroupName);
                });
    }

    @Test
    void adminCanUpdateMenuItem() {
        // When menu item and group exist
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);
        ResponseMenuItemDto menuItem = createMenuItem(menuGroup, adminToken);

        // Update the menu item
        restTestClient.put()
                .uri(MENU_ITEM_URI + "/" + menuItem.id())
                .headers(h -> h.setBearerAuth(adminToken))
                .body(new RequestMenuItemDto(
                        menuGroup.id(),
                        "Hot dogs",
                        "Yummy test hot dog",
                        new BigDecimal("9.99"),
                        null
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMenuItemDto.class)
                .value(menuItemRes -> {
                    assertThat(menuItemRes).isNotNull();
                    assertThat(menuItemRes.name()).isEqualTo("Hot dogs");
                    assertThat(menuItemRes.description()).isEqualTo("Yummy test hot dog");
                    assertThat(menuItemRes.price()).isEqualTo(new BigDecimal("9.99"));
                });
    }

    @Test
    void customerCannotUpdateMenuGroup() {
        // Assuming an admin already created a menu group
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);

        // A customer should not be able to update that menu group
        String newMenuGroupName = "Appetizers";

        restTestClient.patch()
                .uri(MENU_GROUP_URI + "/" + menuGroup.id())
                .headers(h -> h.setBearerAuth(customerToken))
                .body(new UpdateMenuGroupDto(newMenuGroupName))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Forbidden");
                    assertThat(error.errorMessage()).isEqualTo("Access Denied");
                });
    }

    @Test
    void customerCannotUpdateMenuItem() {
        // Assuming an admin already created a menu group and item
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);
        ResponseMenuItemDto menuItem = createMenuItem(menuGroup, adminToken);

        // A customer should not be able to edit the newly created menu item
        restTestClient.put()
                .uri(MENU_ITEM_URI + "/" + menuItem.id())
                .headers(h -> h.setBearerAuth(customerToken))
                .body(new RequestMenuItemDto(
                        menuGroup.id(),
                        "Hot dogs",
                        "Yummy test hot dog",
                        new BigDecimal("9.99"),
                        null
                ))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Forbidden");
                    assertThat(error.errorMessage()).isEqualTo("Access Denied");
                });
    }

    @Test
    void adminCanDeleteMenuGroup() {
        // When a menu group is created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);

        // Then delete menu group
        restTestClient.delete().uri(MENU_GROUP_URI + "/" + menuGroup.id())
                .headers(h -> h.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void adminCanDeleteMenuItem() {
        // When menu group and item are created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);
        ResponseMenuItemDto menuItem = createMenuItem(menuGroup, adminToken);

        // Then delete menu item
        restTestClient.delete().uri(MENU_ITEM_URI + "/" + menuItem.id())
                .headers(h -> h.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void customerCannotDeleteMenuGroup() {
        // When a menu group is created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);

        // Then a customer should not be able to delete the menu group
        restTestClient.delete().uri(MENU_GROUP_URI + "/" + menuGroup.id())
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Forbidden");
                    assertThat(error.errorMessage()).isEqualTo("Access Denied");
                });
    }

    @Test
    void customerCannotDeleteMenuItem() {
        // When menu group and item are created
        ResponseMenuGroupDto menuGroup = createMenuGroup(adminToken);
        ResponseMenuItemDto menuItem = createMenuItem(menuGroup, adminToken);

        // Then a customer should not be able to delete the menu group
        restTestClient.delete().uri(MENU_ITEM_URI + "/" + menuItem.id())
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponseDto.class)
                .value(error -> {
                    assertThat(error).isNotNull();
                    assertThat(error.errorName()).isEqualTo("Forbidden");
                    assertThat(error.errorMessage()).isEqualTo("Access Denied");
                });
    }
}
