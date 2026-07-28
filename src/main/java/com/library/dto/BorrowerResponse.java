package com.library.dto;

import com.library.model.Borrower;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response DTO for Borrower entity
 * Using Lombok builder pattern for object creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A registered library borrower")
public class BorrowerResponse implements Serializable {

    private static final long serialVersionUID = 8213527857359858437L;

    @Schema(description = "Unique identifier of the borrower", example = "1")
    private Long id;

    @Schema(description = "Full name of the borrower", example = "Ada Lovelace")
    private String name;

    @Schema(description = "Unique email address of the borrower", example = "ada@example.com")
    private String email;

    public static BorrowerResponse from(Borrower borrower) {
        return BorrowerResponse.builder()
                .id(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .build();
    }
}