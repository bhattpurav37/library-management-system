package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request body for {@code POST /api/v1/auth/token}. Carries the role to mint a token for
 * (LIBRARIAN or BORROWER). This is a dev/example convenience endpoint with no real credentials.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for minting an access token for a given role")
public class TokenRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "role is required")
    @Schema(description = "Role to mint a token for", example = "LIBRARIAN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;
}
