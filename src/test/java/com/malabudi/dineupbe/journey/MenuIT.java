package com.malabudi.dineupbe.journey;

import com.malabudi.dineupbe.AbstractTestcontainers;
import com.malabudi.dineupbe.auth.data.AuthenticationRequest;
import com.malabudi.dineupbe.auth.data.AuthenticationResponse;
import com.malabudi.dineupbe.auth.data.RegisterRequest;
import com.malabudi.dineupbe.common.util.Role;
import com.malabudi.dineupbe.menu.dto.RequestMenuItemDto;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class MenuIT extends AbstractTestcontainers {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    private String customerToken;

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
    }

    @Test
    void customerCannotCreateMenuItem() {
        // Given: Customer is authenticated
        HttpHeaders customerHeaders = new HttpHeaders();
        customerHeaders.setBearerAuth(customerToken);

        // When: Customer attempts to create menu item
        RequestMenuItemDto menuItem = new RequestMenuItemDto(
                1L,
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
