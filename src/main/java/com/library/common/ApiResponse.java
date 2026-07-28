package com.library.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response envelope. Used by the global exception handler so every error response
 * (validation, not-found, conflict, internal) shares one shape. Successful responses are returned
 * as raw DTOs from the controllers, so this class is error-only by design.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response envelope")
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = -8241732663905578307L;

    @Schema(description = "Timestamp when the response was generated", example = "2026-07-28T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code of the response", example = "409")
    private int status;

    @Schema(description = "Short error category", example = "Conflict",
            allowableValues = {"BadRequest", "ValidationError", "NotFound", "Conflict",
                    "MethodNotAllowed", "InternalServerError"})
    private String error;

    @Schema(description = "Human-readable message describing the error", example = "book 1 is already on loan")
    private String message;

    @Schema(description = "Field-level validation errors (present only on validation failures)")
    private List<FieldError> fieldErrors;

    /**
     * Creates an error response.
     */
    public static <T> ApiResponse<T> error(int status, String error, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .build();
    }

    /**
     * Creates an error response with field errors.
     */
    public static <T> ApiResponse<T> error(int status, String error, String message, List<FieldError> fieldErrors) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .fieldErrors(fieldErrors)
                .build();
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp != null ? this.timestamp : LocalDateTime.now();
    }
}
