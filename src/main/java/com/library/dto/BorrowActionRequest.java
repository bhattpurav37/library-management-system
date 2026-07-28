package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for borrow/return actions
 * Using Lombok builder pattern for object creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for borrowing or returning a book on behalf of a borrower")
public class BorrowActionRequest implements Serializable {

    private static final long serialVersionUID = -7553312903507202741L;

    @NotNull(message = "borrowerId is required")
    @Schema(description = "ID of the borrower performing the action", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long borrowerId;
}