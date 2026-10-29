package com.library.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Self-contained JWT wiring for the resource server. There is no external Authorization Server
 * (this is an example app), so a single HS256 symmetric key is shared between the
 * {@link JwtDecoder} (validates incoming tokens) and the {@link JwtEncoder} (signs tokens issued
 * by {@link JwtTokenService} via the /api/v1/auth/token endpoint). Both read the same
 * {@code app.security.jwt.secret} property.
 * <p>
 * The {@code jwtAuthConverter} maps a {@code roles} claim to {@code ROLE_} authorities — Spring's
 * default converter only understands {@code scope}/{@code scp}, but this app's tokens carry
 * {@code roles} (e.g. {@code ["LIBRARIAN"]}), so without this {@code hasRole("LIBRARIAN")} would
 * never match.
 */
@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(@Value("${app.security.jwt.secret}") String base64Secret) {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Secret);
        return new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(jwtSecretKey).keyID("library-key").build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    /**
     * Converts a validated {@link Jwt} into an authentication token whose authorities come from the
     * {@code roles} claim (plus any scopes, for completeness). {@code hasRole("LIBRARIAN")} then
     * resolves to authority {@code ROLE_LIBRARIAN}.
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("ROLE_");
        // Spring uppercases the claim value when prefixing, but role values in this app are already
        // uppercase; leave defaults so "librarian" -> "ROLE_LIBRARIAN" too.

        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        // defaults: claim "scope"/"scp", prefix "SCOPE_"

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.addAll(rolesConverter.convert(jwt));
            authorities.addAll(scopesConverter.convert(jwt));
            return authorities;
        });
        return converter;
    }
}
