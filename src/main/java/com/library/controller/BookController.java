package com.library.controller;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.model.Book;
import com.library.service.interfaces.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "Book registration and lookup operations")
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Register a new book",
            description = "Registers a new physical copy of a book. Multiple copies of the same ISBN are allowed, but the title and author must match."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book registered successfully",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":400,\"error\":\"ValidationError\",\"message\":\"Request validation failed\",\"fieldErrors\":[{\"field\":\"isbn\",\"message\":\"isbn is required\",\"objectName\":\"bookRequest\",\"rejectedValue\":\"\"}]}"))),
            @ApiResponse(responseCode = "409", description = "ISBN conflict - same ISBN with different title/author",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":409,\"error\":\"Conflict\",\"message\":\"ISBN conflict - title or author mismatch\"}")))
    })
    @PostMapping
    public ResponseEntity<BookResponse> register(@Valid @RequestBody BookRequest request) {
        LOGGER.info(String.format("Registering book with isbn: [%s]", request.getIsbn()));
        Book book = new Book();
        book.setIsbn(request.getIsbn().trim());
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        Book saved = bookService.register(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(saved, true));
    }

    @Operation(
            summary = "List all books",
            description = "Returns all registered books with their availability status"
    )
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
            content = @Content(array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                    schema = @Schema(implementation = BookResponse.class))))
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        LOGGER.info(String.format("Fetching all books"));
        // Single query for all borrowed copies, then map availability from the set - avoids an N+1
        // of one availability query per book.
        Set<Long> borrowedIds = bookService.findBorrowedBookIds();
        List<BookResponse> books = bookService.getAll().stream()
                .map(b -> BookResponse.from(b, !borrowedIds.contains(b.getId())))
                .toList();
        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "Get a book by ID",
            description = "Retrieves a single book copy by its ID, including availability status"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":404,\"error\":\"NotFound\",\"message\":\"Book not found with id: 99\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable Long id) {
        LOGGER.info(String.format("Fetching book by id: [%s]", id));
        Book book = bookService.getById(id);
        return ResponseEntity.ok(BookResponse.from(book, bookService.isAvailable(id)));
    }
}
