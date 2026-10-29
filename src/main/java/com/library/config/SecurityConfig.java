package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security for the API: a stateless OAuth2 resource server validating HS256 JWTs issued by the
 * dev-only /api/v1/auth/token endpoint (see {@link JwtTokenService}).
 * <p>
 * Matcher order matters: the borrow/return rules are declared <em>before</em> the broad
 * {@code /api/v1/books/**} matcher, otherwise first-match-wins would lock borrow/return to
 * LIBRARIAN and the BORROWER rule would be dead code.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain chain(HttpSecurity http,
                                     Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter) throws Exception {
        http.csrf(c -> c.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/api/v1/auth/token").permitAll()
                        // Swagger UI + OpenAPI docs are public so the docs page itself loads; the
                        // documented API endpoints behind them are still role-protected below.
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/books/*/borrow", "/api/v1/books/*/return").hasRole("BORROWER")
                        .requestMatchers("/api/v1/books/**", "/api/v1/borrowers/**").hasRole("LIBRARIAN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtAuthConverter)));
        return http.build();
    }
}
