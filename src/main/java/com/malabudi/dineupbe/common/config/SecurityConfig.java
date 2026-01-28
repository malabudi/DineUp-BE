package com.malabudi.dineupbe.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Lazy JWTAuthenticationFilter jwtAuthFilter
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authRequests -> authRequests
                    .requestMatchers("/api/v1/auth/**").permitAll()

                    // Define permissions here for which role can call which endpoint
                    .requestMatchers(HttpMethod.GET, "/api/v1/menu-items/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/menu-items/**").hasAnyRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/menu-items/**").hasAnyRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/menu-items/**").hasAnyRole("ADMIN")

                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKey originalKey = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(originalKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKey originalKey = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA256");
        return NimbusJwtEncoder.withSecretKey(originalKey).build();
    }
}
