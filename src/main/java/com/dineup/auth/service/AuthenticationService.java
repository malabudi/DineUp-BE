package com.dineup.auth.service;

import com.dineup.auth.data.AuthenticationRequest;
import com.dineup.auth.data.AuthenticationResponse;
import com.dineup.auth.data.RegisterRequest;
import com.dineup.auth.exception.InvalidCredentialsException;
import com.dineup.auth.exception.UserAlreadyExistsException;
import com.dineup.common.config.JwtService;
import com.dineup.user.exception.UserNotFoundException;
import com.dineup.user.repository.UserRepository;
import com.dineup.common.util.Role;
import lombok.RequiredArgsConstructor;
import com.dineup.user.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Register new user
    public AuthenticationResponse register(RegisterRequest request) {

        // Check if user exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getRole() != null ? request.getRole() : Role.CUSTOMER,
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // Authenticate existing user
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
