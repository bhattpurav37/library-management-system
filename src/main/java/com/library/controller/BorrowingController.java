package com.library.controller;

import com.library.dto.BorrowActionRequest;
import com.library.dto.BorrowRecordResponse;
import com.library.model.BorrowRecord;
import com.library.service.interfaces.BorrowingService;
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

@RestController
@RequestMapping("/api/v1/books/{bookId}")
@Tag(name = "Borrowing", description = "Book borrowing and return operations")
public class BorrowingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowingController.class);

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @Operation(
            summary = "Borrow a book",
            description = "Borrows a book on behalf of a borrower. Uses pessimistic locking to prevent concurrent borrows of the same book."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book borrowed successfully",
                    content = @Content(schema = @Schema(implementation = BorrowRecordResponse.class))),
            @ApiResponse(responseCode = "404", description = "Book or borrower not found",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":404,\"error\":\"NotFound\",\"message\":\"Book not found with id: 99\"}"))),
            @ApiResponse(responseCode = "409", description = "Book is already on loan",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":409,\"error\":\"Conflict\",\"message\":\"book 1 is already on loan\"}")))
    })
    @PostMapping("/borrow")
    public ResponseEntity<BorrowRecordResponse> borrow(
            @Parameter(description = "Book ID to borrow", required = true, example = "1") @PathVariable Long bookId,
            @Valid @RequestBody BorrowActionRequest request) {
        LOGGER.info(String.format("Borrowing book id: [%s] for borrower id: [%s]", bookId, request.getBorrowerId()));
        BorrowRecord record = borrowingService.borrow(bookId, request.getBorrowerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(BorrowRecordResponse.from(record));
    }

    @Operation(
            summary = "Return a book",
            description = "Returns a previously borrowed book, making it available for future loans"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully",
                    content = @Content(schema = @Schema(implementation = BorrowRecordResponse.class))),
            @ApiResponse(responseCode = "404", description = "No active loan found for the given book and borrower",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":404,\"error\":\"NotFound\",\"message\":\"No active loan found for book 1 and borrower 2\"}")))
    })
    @PostMapping("/return")
    public ResponseEntity<BorrowRecordResponse> returnBook(
            @Parameter(description = "Book ID to return", required = true, example = "1") @PathVariable Long bookId,
            @Valid @RequestBody BorrowActionRequest request) {
        LOGGER.info(String.format("Returning book id: [%s] for borrower id: [%s]", bookId, request.getBorrowerId()));
        BorrowRecord record = borrowingService.returnBook(bookId, request.getBorrowerId());
        return ResponseEntity.ok(BorrowRecordResponse.from(record));
    }
}
