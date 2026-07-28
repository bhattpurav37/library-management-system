package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for Borrower registration
 * Using Lombok builder pattern for object creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for registering a new borrower")
public class BorrowerRequest implements Serializable {

    private static final long serialVersionUID = -8216599594045201256L;

    @NotBlank(message = "name is required")
    @Schema(description = "Full name of the borrower", example = "Ada Lovelace", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @Schema(description = "Unique email address of the borrower (case-insensitive, stored lowercased)",
            example = "ada@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}