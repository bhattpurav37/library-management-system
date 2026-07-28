package com.library.controller;

import com.library.dto.BorrowerRequest;
import com.library.dto.BorrowerResponse;
import com.library.model.Borrower;
import com.library.service.interfaces.BorrowerService;
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
@RequestMapping("/api/v1/borrowers")
@Tag(name = "Borrowers", description = "Borrower registration and lookup operations")
public class BorrowerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowerController.class);

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @Operation(
            summary = "Register a new borrower",
            description = "Registers a new borrower with a unique email address"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Borrower registered successfully",
                    content = @Content(schema = @Schema(implementation = BorrowerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate email",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":400,\"error\":\"ValidationError\",\"message\":\"Request validation failed\",\"fieldErrors\":[{\"field\":\"email\",\"message\":\"email must be a valid email address\",\"objectName\":\"borrowerRequest\",\"rejectedValue\":\"not-an-email\"}]}")))
    })
    @PostMapping
    public ResponseEntity<BorrowerResponse> register(@Valid @RequestBody BorrowerRequest request) {
        LOGGER.info(String.format("Registering borrower with email: [%s]", request.getEmail()));
        Borrower borrower = new Borrower();
        borrower.setName(request.getName().trim());
        borrower.setEmail(request.getEmail().trim().toLowerCase());
        Borrower saved = borrowerService.register(borrower);
        return ResponseEntity.status(HttpStatus.CREATED).body(BorrowerResponse.from(saved));
    }

    @Operation(
            summary = "Get a borrower by ID",
            description = "Retrieves a borrower by their unique ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrower found",
                    content = @Content(schema = @Schema(implementation = BorrowerResponse.class))),
            @ApiResponse(responseCode = "404", description = "Borrower not found",
                    content = @Content(schema = @Schema(implementation = com.library.common.ApiResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-07-28T10:15:30\",\"status\":404,\"error\":\"NotFound\",\"message\":\"Borrower not found with id: 99\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BorrowerResponse> getById(
            @Parameter(description = "Borrower ID", required = true, example = "1") @PathVariable Long id) {
        LOGGER.info(String.format("Fetching borrower by id: [%s]", id));
        return ResponseEntity.ok(BorrowerResponse.from(borrowerService.getById(id)));
    }
}
