package com.library.controller;

import com.library.config.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mints real, signed JWTs for the integration tests so they exercise the full resource-server
 * pipeline (decoder + role converter + matchers) rather than mocking authentication. The signing
 * key is the same one the running app validates against.
 */
@Component
public class SecurityTestSupport {

    private final JwtTokenService jwtTokenService;

    @Autowired
    public SecurityTestSupport(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    public String librarianBearer() {
        return "Bearer " + jwtTokenService.generate("test-librarian", "LIBRARIAN");
    }

    public String borrowerBearer() {
        return "Bearer " + jwtTokenService.generate("test-borrower", "BORROWER");
    }
}
