package com.dineup.auth;

import com.dineup.auth.data.AuthenticationRequest;
import com.dineup.auth.data.AuthenticationResponse;
import com.dineup.auth.data.RegisterRequest;
import com.dineup.auth.exception.InvalidCredentialsException;
import com.dineup.auth.exception.UserAlreadyExistsException;
import com.dineup.auth.service.AuthenticationService;
import com.dineup.common.config.JwtService;
import com.dineup.common.util.Role;
import com.dineup.user.model.User;
import com.dineup.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService underTest;

    private RegisterRequest registerRequest;
    private AuthenticationRequest authenticationRequest;
    private User testUser;

    private static final String TEST_EMAIL = "johndoe@test.com";
    private static final String TEST_JWT_TOKEN = "jwt-token-mock";

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "John",
                "Doe",
                "johndoe@test.com",
                Role.CUSTOMER,
                "password"
        );

        authenticationRequest = new AuthenticationRequest(
                "johndoe@test.com",
                "password"
        );

        testUser = new User(
                "John",
                "Doe",
                TEST_EMAIL,
                Role.CUSTOMER,
                "password"
        );
    }

    @Test
    void register_ShouldSaveUser_WhenUserDoesNotExist() {
        // Arrange
        when(jwtService.generateToken(any(User.class))).thenReturn(TEST_JWT_TOKEN);

        // Act
        AuthenticationResponse response = underTest.register(registerRequest);

        // Assert
        verify(userRepository).save(any(User.class));
        assertThat(response.getToken()).isNotNull();
        assertThat(response.getToken()).isEqualTo(TEST_JWT_TOKEN);
    }

    @Test
    void register_ShouldThrowException_WhenUserAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> underTest.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email " + TEST_EMAIL + " already exists");
    }

    @Test
    void authenticate_ShouldAuthenticateUser_WhenCredentialsCorrect() {
        // Arrange
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn(TEST_JWT_TOKEN);

        // Act
        AuthenticationResponse response = underTest.authenticate(authenticationRequest);

        // Assert
        verify(userRepository).findByEmail(any(String.class));
        assertThat(response.getToken()).isNotNull();
        assertThat(response.getToken()).isEqualTo(TEST_JWT_TOKEN);
    }

    @Test
    void authenticate_ShouldThrowException_WhenCredentialsIncorrect() {
        // Arrange
        doThrow(new RuntimeException("Bad credentials")).when(authenticationManager).authenticate(any());

        // Act & Assert
        assertThatThrownBy(() -> underTest.authenticate(authenticationRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFoundAfterAuth() {
        // Arrange
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> underTest.authenticate(authenticationRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");
    }
}
