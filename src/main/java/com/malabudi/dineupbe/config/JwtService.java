package com.malabudi.dineupbe.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String extractUsername(String jwtToken) {
        Jwt jwt = jwtDecoder.decode(jwtToken);
        return jwt.getSubject();
    }

    public Instant extractExpiration(String jwtToken) {
        Jwt jwt = jwtDecoder.decode(jwtToken);
        return jwt.getExpiresAt();
    }

    public <T> T extractClaim(String jwtToken, String claimName) {
        Jwt jwt = jwtDecoder.decode(jwtToken);
        return jwt.getClaim(claimName);
    }

    public Map<String, Object> extractAllClaims(String jwtToken) {
        Jwt jwt = jwtDecoder.decode(jwtToken);
        return jwt.getClaims();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, new HashMap<>());
    }

    public String generateToken(
            UserDetails userDetails,
            Map<String, Object> extraClaims
    ) {
        Instant now = Instant.now();

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("dineup")
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(24, ChronoUnit.HOURS));

        // Finalize by adding extra claims
        extraClaims.forEach(claimsBuilder::claim);

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    // Is token not expired? Does it belong to the username?
    public boolean isTokenValid(
            String jwtToken,
            UserDetails userDetails
    ) {
        final String username = this.extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername())) && !this.isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return this.extractExpiration(jwtToken).isBefore(Instant.now());
    }
}
