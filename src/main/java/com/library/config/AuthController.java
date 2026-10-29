package com.library.config;

import com.library.dto.TokenRequest;
import com.library.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Dev/example-only endpoint to mint access tokens for a given role. There are no real credentials
 * here — it exists purely so the secured API can be exercised from Swagger or curl without a real
 * Authorization Server.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Token issuance (dev/example only)")
public class AuthController {

    private static final Set<String> ALLOWED_ROLES = Set.of("LIBRARIAN", "BORROWER");

    private final JwtTokenService jwtTokenService;

    public AuthController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Operation(summary = "Mint an access token for a role",
            description = "Returns a signed HS256 JWT carrying the requested role. No credentials are checked — "
                    + "this is a convenience endpoint for the example app only.")
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@Valid @RequestBody TokenRequest request) {
        String role = request.getRole().trim().toUpperCase();
        if (!ALLOWED_ROLES.contains(role)) {
            throw new IllegalArgumentException("role must be one of: " + ALLOWED_ROLES);
        }
        String token = jwtTokenService.generate(role.toLowerCase(), role);
        return ResponseEntity.ok(TokenResponse.builder().token(token).build());
    }
}
