package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response for {@code POST /api/v1/auth/token}: a signed HS256 JWT to send as a Bearer token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "An issued access token")
public class TokenResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Bearer access token", example = "eyJhbGciOi...")
    private String token;
}
