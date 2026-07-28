package com.library.dto;

import com.library.model.BorrowRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response DTO for BorrowRecord entity
 * Using Lombok builder pattern for object creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single loan record representing one borrow (and optional return) of a book copy")
public class BorrowRecordResponse implements Serializable {

    private static final long serialVersionUID = -8284385148796543615L;

    @Schema(description = "Unique identifier of the loan record", example = "10")
    private Long id;

    @Schema(description = "ID of the borrowed book copy", example = "1")
    private Long bookId;

    @Schema(description = "ID of the borrower who holds the loan", example = "1")
    private Long borrowerId;

    @Schema(description = "Timestamp when the book was borrowed", example = "2026-07-28T10:15:30")
    private LocalDateTime borrowedAt;

    @Schema(description = "Timestamp when the book was returned (null while the loan is active)",
            example = "2026-07-29T09:00:00")
    private LocalDateTime returnedAt;

    public static BorrowRecordResponse from(BorrowRecord record) {
        return BorrowRecordResponse.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .borrowerId(record.getBorrower().getId())
                .borrowedAt(record.getBorrowedAt())
                .returnedAt(record.getReturnedAt())
                .build();
    }
}