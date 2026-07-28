package com.library.dto;

import com.library.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response DTO for Book entity
 * Using Lombok builder pattern for object creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single physical book copy with its current availability")
public class BookResponse implements Serializable {

    private static final long serialVersionUID = -123679691985846943L;

    @Schema(description = "Unique identifier of this physical copy", example = "1")
    private Long id;

    @Schema(description = "ISBN of the book (shared by all copies of the same work)", example = "9780132350884")
    private String isbn;

    @Schema(description = "Title of the book", example = "Clean Code")
    private String title;

    @Schema(description = "Author of the book", example = "Robert C. Martin")
    private String author;

    @Schema(description = "Whether this copy is currently available to borrow (false while on loan)",
            example = "true")
    private boolean available;

    public static BookResponse from(Book book, boolean available) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .available(available)
                .build();
    }
}