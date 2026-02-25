package com.dineup.journey;

import com.dineup.BaseIT;
import com.dineup.common.dto.ErrorResponseDto;
import com.dineup.common.util.OrderStatus;
import com.dineup.menu.dto.CreateMenuGroupDto;
import com.dineup.menu.dto.RequestMenuItemDto;
import com.dineup.menu.dto.ResponseMenuGroupDto;
import com.dineup.menu.dto.ResponseMenuItemDto;
import com.dineup.menu.repository.MenuGroupRepository;
import com.dineup.menu.repository.MenuItemRepository;
import com.dineup.order.dto.CreateOrderDto;
import com.dineup.order.dto.RequestLineItemDto;
import com.dineup.order.dto.ResponseOrderDto;
import com.dineup.order.dto.UpdateOrderStatusDto;
import com.dineup.order.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
class OrderIT extends BaseIT {

    private static final String MENU_GROUP_URI = "/api/v1/menu-groups";
    private static final String MENU_ITEM_URI = "/api/v1/menu-items";
    private static final String ORDER_URI = "/api/v1/orders";
    private static final String TEST_ITEM_NAME = "Burger";
    private static final String TEST_ITEM_DESCRIPTION = "Tasty test burger";
    private static final BigDecimal TEST_ITEM_PRICE = new BigDecimal("8.99");
    private static final String TEST_MENU_GROUP_NAME = "Main Course";
    private static final BigDecimal EXPECTED_ORDER_TOTAL = new BigDecimal("26.97");

    List<RequestLineItemDto> testItems;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        super.setUpTestUsers();

        // Set up menu
        CreateMenuGroupDto createMenuGroupDto = new CreateMenuGroupDto(
                TEST_MENU_GROUP_NAME
        );

        ResponseMenuGroupDto menuGroup = restTestClient.post()
                .uri(MENU_GROUP_URI)
                .headers(h -> h.setBearerAuth(adminToken))
                .body(createMenuGroupDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuGroupDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(menuGroup).isNotNull();

        RequestMenuItemDto reqMenuItem = new RequestMenuItemDto(
                menuGroup.id(),
                TEST_ITEM_NAME,
                TEST_ITEM_DESCRIPTION,
                TEST_ITEM_PRICE,
                null
        );

        ResponseMenuItemDto menuItem = restTestClient.post()
                .uri(MENU_ITEM_URI)
                .headers(h -> h.setBearerAuth(adminToken))
                .body(reqMenuItem)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseMenuItemDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(menuItem).isNotNull();

        // Set up test order
        testItems = List.of(
                new RequestLineItemDto(menuItem.id(), 3)
        );
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        menuItemRepository.deleteAll();
        menuGroupRepository.deleteAll();
    }

    @Test
    void customerCanPlaceOrder() {
        restTestClient.post()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(new CreateOrderDto(testItems))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ResponseOrderDto.class)   // deserialize as raw string first
                .value(orderRes -> {
                    assertThat(orderRes).isNotNull();
                    assertThat(orderRes.id()).isNotNull();
                    assertThat(orderRes.status()).isEqualTo(OrderStatus.PENDING);
                    assertThat(orderRes.total()).isEqualTo(EXPECTED_ORDER_TOTAL);
                    assertThat(orderRes.items()).hasSize(1);
                });
    }

    @Test
    void adminCanFetchAllOrders() {
        // Place orders first
        restTestClient.post()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(new CreateOrderDto(testItems))
                .exchange();

        // As an admin, fetch all orders placed
        restTestClient.get()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(adminToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseOrderDto[].class)
                .value(orderRes -> {
                    assertThat(orderRes).isNotNull();
                    assertThat(orderRes).hasSize(1);
                    assertThat(orderRes[0].id()).isNotNull();
                    assertThat(orderRes[0].status()).isEqualTo(OrderStatus.PENDING);
                    assertThat(orderRes[0].total()).isEqualTo(EXPECTED_ORDER_TOTAL);
                });
    }

    @Test
    void customerCannotFetchAllOrders() {
        restTestClient.get()
                .uri(ORDER_URI)
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
    void customerCanFetchTheirOrders() {
        // Place an order first as a customer
        restTestClient.post()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(new CreateOrderDto(testItems))
                .exchange();


        // Fetch order as a customer
        restTestClient.get()
                .uri(ORDER_URI + "/my-orders")
                .headers(h -> h.setBearerAuth(customerToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseOrderDto[].class)
                .value(orderRes -> {
                    assertThat(orderRes).isNotNull();
                    assertThat(orderRes).hasSize(1);
                    assertThat(orderRes[0].id()).isNotNull();
                    assertThat(orderRes[0].status()).isEqualTo(OrderStatus.PENDING);
                    assertThat(orderRes[0].total()).isEqualTo(EXPECTED_ORDER_TOTAL);
                });
    }

    @Test
    void adminCanUpdateOrderStatus() {
        // Place an order as a customer first
        ResponseOrderDto order = restTestClient.post()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(new CreateOrderDto(testItems))
                .exchange()
                .expectBody(ResponseOrderDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(order).isNotNull();

        // Update order status as an admin
        restTestClient.patch()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(adminToken))
                .body(
                        new UpdateOrderStatusDto(
                                order.id(),
                                OrderStatus.COMPLETE
                        )
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseOrderDto.class)
                .value(orderRes -> {
                    assertThat(orderRes).isNotNull();
                    assertThat(orderRes.status()).isEqualTo(OrderStatus.COMPLETE);
                });
    }

    @Test
    void customerCannotUpdateOrderStatus() {
        restTestClient.patch()
                .uri(ORDER_URI)
                .headers(h -> h.setBearerAuth(customerToken))
                .body(
                        new UpdateOrderStatusDto(
                                9999L,
                                OrderStatus.COMPLETE
                        )
                )
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
