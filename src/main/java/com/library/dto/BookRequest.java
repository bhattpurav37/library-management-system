package com.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Request DTO for Book registration
 * Using Lombok builder pattern for object creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for registering a new book")
public class BookRequest implements Serializable {

    private static final long serialVersionUID = -4496124150247494439L;

    // covers plain 10/13 digit ISBNs and the hyphenated form, good enough for this exercise
    @NotBlank(message = "isbn is required")
    @Pattern(regexp = "^(97[89])?\\d{9}(\\d|X)$|^[0-9\\-]{10,17}$", message = "isbn is not a valid format")
    @Schema(description = "ISBN of the book (10 or 13 digit, or hyphenated format)", example = "9780132350884", requiredMode = Schema.RequiredMode.REQUIRED)
    private String isbn;

    @NotBlank(message = "title is required")
    @Schema(description = "Title of the book", example = "Clean Code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "author is required")
    @Pattern(regexp = "^[\\p{L}\\d][\\p{L}\\d .'-]*$", message = "author must not contain special characters")
    @Schema(description = "Author of the book", example = "Robert C. Martin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String author;
}