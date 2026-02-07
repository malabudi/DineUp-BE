package com.malabudi.dineupbe.journey;

import com.malabudi.dineupbe.auth.data.AuthenticationRequest;
import com.malabudi.dineupbe.auth.data.AuthenticationResponse;
import com.malabudi.dineupbe.auth.data.RegisterRequest;
import com.malabudi.dineupbe.common.util.OrderStatus;
import com.malabudi.dineupbe.common.util.Role;
import com.malabudi.dineupbe.menu.dto.CreateMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.ResponseMenuGroupDto;
import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
import com.malabudi.dineupbe.order.dto.CreateOrderDto;
import com.malabudi.dineupbe.order.dto.ResponseLineItemDto;
import com.malabudi.dineupbe.order.dto.RequestLineItemDto;
import com.malabudi.dineupbe.order.dto.ResponseOrderDto;
import com.malabudi.dineupbe.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
public class OrderIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String customerToken;
    private String adminToken;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Register and login as a customer
        RegisterRequest registerCustomerRequest = new RegisterRequest(
                "Customer",
                "User",
                "customer@test.com",
                Role.CUSTOMER,
                "testpassword123"
        );

        restTemplate.postForEntity(
                "/api/v1/auth/register",
                registerCustomerRequest,
                AuthenticationResponse.class
        );

        AuthenticationRequest authCustomerRequest = new AuthenticationRequest(
          "customer@test.com",
          "testpassword123"
        );

        ResponseEntity<AuthenticationResponse> customerLoginResponse = restTemplate.postForEntity(
                "/api/v1/auth/authenticate",
                authCustomerRequest,
                AuthenticationResponse.class
        );

        customerToken = customerLoginResponse.getBody() != null ? customerLoginResponse.getBody().getToken() : null;

        // Register and login as an admin
        RegisterRequest registerAdminRequest = new RegisterRequest(
                "Admin",
                "User",
                "adminuser@test.com",
                Role.ADMIN,
                "testpassword123"
        );

        restTemplate.postForEntity(
                "/api/v1/auth/register",
                registerAdminRequest,
                AuthenticationResponse.class
        );

        AuthenticationRequest authAdminRequest = new AuthenticationRequest(
                "adminuser@test.com",
                "testpassword123"
        );

        ResponseEntity<AuthenticationResponse> adminLoginResponse = restTemplate.postForEntity(
                "/api/v1/auth/authenticate",
                authAdminRequest,
                AuthenticationResponse.class
        );

        adminToken = adminLoginResponse.getBody() != null ? adminLoginResponse.getBody().getToken() : null;
    }

    @Test
    void customerCanPlaceOrder() {
        // Given: Admin creates one menu group and one menu item
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        
        CreateMenuGroupDto mainCourseRequest = new CreateMenuGroupDto(
                "Main Course"
        );
        HttpEntity<CreateMenuGroupDto> mainCourseEntity = new HttpEntity<>(mainCourseRequest, adminHeaders);

        ResponseEntity<ResponseMenuGroupDto> mainCourseResponse =
                restTemplate.postForEntity(
                        "/api/v1/menu-group",
                        mainCourseEntity,
                        ResponseMenuGroupDto.class
                );
        
        RequestMenuItemDto hamburgerRequest = new RequestMenuItemDto(
                null,
                mainCourseResponse.getBody() != null ? mainCourseResponse.getBody().id() : null,
                "Hamburger",
                "A beef patty placed inside a pretzel bun, served with lettuce, tomato, onion, pickles, and famous housemade aioli.",
                new BigDecimal("9.99"),
                null
        );
        HttpEntity<RequestMenuItemDto> hamburgerEntity = new HttpEntity<>(hamburgerRequest, adminHeaders);

        ResponseEntity<RequestMenuItemDto> hamburgerResponse =
                restTemplate.postForEntity(
                        "/api/v1/menu-items",
                        hamburgerEntity,
                        RequestMenuItemDto.class
                );
        Long menuItemId = hamburgerResponse.getBody() != null ? hamburgerResponse.getBody().id() : null;

        // When: Customer places order
        HttpHeaders customerHeaders = new HttpHeaders();
        customerHeaders.setBearerAuth(customerToken);

        CreateOrderDto orderRequest = new CreateOrderDto(
                List.of(new RequestLineItemDto(menuItemId, 3))
        );

        HttpEntity<CreateOrderDto> orderEntity = new HttpEntity<>(orderRequest, customerHeaders);
        ResponseEntity<ResponseOrderDto> orderResponse = restTemplate.postForEntity(
                "/api/v1/order",
                orderEntity,
                ResponseOrderDto.class
        );

        // Then: Order is successfully placed
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseOrderDto order = orderResponse.getBody();
        assertThat(order).isNotNull();
        assertThat(order.id()).isNotNull();
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.total()).isEqualByComparingTo(new BigDecimal("29.97"));
        assertThat(order.items()).hasSize(1);
        assertThat(order.items().getFirst())
                .returns("Hamburger", ResponseLineItemDto::menuItemName)
                .returns(3, ResponseLineItemDto::quantity);
    }

    // TODO: Move to MenuIntegrationTests.java
    @Test
    void customerCannotCreateMenuItem() {
        // Given: Customer is authenticated
        HttpHeaders customerHeaders = new HttpHeaders();
        customerHeaders.setBearerAuth(customerToken);

        // When: Customer attempts to create menu item
        RequestMenuItemDto menuItem = new RequestMenuItemDto(
                null,
                null,
                "Burger",
                "Tasty test burger",
                new BigDecimal("8.99"),
                null
        );
        HttpEntity<RequestMenuItemDto> menuItemEntity = new HttpEntity<>(menuItem, customerHeaders);
        ResponseEntity<RequestMenuItemDto> menuItemResponse = restTemplate.postForEntity(
                "api/v1/menu-items",
                menuItemEntity,
                RequestMenuItemDto.class
        );

        // Then: Deny access to create menu item
        assertThat(menuItemResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
