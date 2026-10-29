package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Mints HS256 access tokens carrying a {@code roles} claim, signed with the same key the resource
 * server validates against (see {@link JwtConfig}). Shared by {@link AuthController} and the
 * integration tests so the signing logic is defined once.
 */
@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long ttlSeconds;

    public JwtTokenService(JwtEncoder jwtEncoder,
                           @Value("${app.security.jwt.access-token-ttl-minutes:60}") long ttlMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.ttlSeconds = ttlMinutes * 60;
    }

    /**
     * @param subject the token subject (e.g. a user id or name)
     * @param role    a role name; stored uppercase in the {@code roles} claim
     */
    public String generate(String subject, String role) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("library-mgmt")
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .claim("roles", List.of(role.toUpperCase()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
