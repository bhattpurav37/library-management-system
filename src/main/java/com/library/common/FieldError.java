package com.library.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Field Error representation for validation errors
 * Following standard Java validation patterns
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@Schema(description = "A single field-level validation error")
public class FieldError implements Serializable {

    private static final long serialVersionUID = -5910947693987625809L;

    @Schema(description = "Name of the field that failed validation", example = "isbn")
    private String field;

    @Schema(description = "Validation message explaining why the field is invalid", example = "isbn is required")
    private String message;

    @Schema(description = "Name of the validated object", example = "bookRequest")
    private String objectName;

    @Schema(description = "The rejected value that triggered the validation error", example = "")
    private Object rejectedValue;

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public FieldError(String field, String message, String objectName, Object rejectedValue) {
        this.field = field;
        this.message = message;
        this.objectName = objectName;
        this.rejectedValue = rejectedValue;
    }
}