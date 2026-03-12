package com.dineup.journey;

import com.dineup.BaseIT;
import com.dineup.auth.data.AuthenticationRequest;
import com.dineup.auth.data.AuthenticationResponse;
import com.dineup.auth.data.RegisterRequest;
import com.dineup.common.dto.ErrorResponseDto;
import com.dineup.common.exception.ResourceConflictException;
import com.dineup.common.util.Role;
import com.dineup.user.model.User;
import com.dineup.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureRestTestClient
class AuthIT extends BaseIT {

    private static final String AUTH_REGISTER_URI = "/api/v1/auth/register";
    private static final String AUTH_AUTHENTICATE_URI = "/api/v1/auth/authenticate";

    @MockitoSpyBean
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterAndThenAuthenticate() {
        // Register User first
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .password("password")
                .build();

        // Act
        AuthenticationResponse registerResponse = restTestClient.post()
                .uri(AUTH_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthenticationResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(registerResponse).isNotNull();
        assertThat(registerResponse.getToken()).isNotBlank();
        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));

        // Authenticate User
        // Arrange
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("testuser@example.com").password("password").build();

        // Act
        AuthenticationResponse authResponse = restTestClient.post()
                .uri(AUTH_AUTHENTICATE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(authRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthenticationResponse.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isNotBlank();
    }

    @Test
    void shouldFailAuthenticationWithWrongCredentials() {
        // Arrange
        super.setUpTestUsers();

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("cust@test.com")
                .password("WrongPassword")
                .build();

        // Act & Assert
        restTestClient.post()
                .uri(AUTH_AUTHENTICATE_URI)
                .body(authRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .role(Role.CUSTOMER)
                .password("password")
                .build();

        userRepository.save(new User(
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getEmail(),
                registerRequest.getRole(),
                registerRequest.getPassword()
        ));

        // Act
        ErrorResponseDto response = restTestClient.post()
                .uri(AUTH_REGISTER_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT.value())
                .expectBody(ErrorResponseDto.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.errorName()).isEqualTo("Conflict");
        assertThat(response.errorMessage()).isEqualTo(
                "User with email " +
                        registerRequest.getEmail() +
                        " already exists"
        );
        assertThat(response.status()).isEqualTo(HttpStatus.CONFLICT.value());
    }
}
