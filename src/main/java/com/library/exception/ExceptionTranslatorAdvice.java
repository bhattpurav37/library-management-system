package com.library.exception;

import com.library.common.ApiResponse;
import com.library.common.FieldError;
import com.library.constants.ApiConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Global Exception Handler
 * Translates exceptions to consistent ApiResponse format
 */
@RestControllerAdvice
public class ExceptionTranslatorAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslatorAdvice.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        LOG.error("Resource not found", ex);
        return new ResponseEntity<>(
            ApiResponse.error(404, "NotFound", ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookAlreadyBorrowed(BookAlreadyBorrowedException ex, HttpServletRequest req) {
        LOG.error("Book already borrowed", ex);
        return new ResponseEntity<>(
            ApiResponse.error(409, "Conflict", ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(IsbnConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleIsbnConflict(IsbnConflictException ex, HttpServletRequest req) {
        LOG.error("ISBN conflict", ex);
        return new ResponseEntity<>(
            ApiResponse.error(409, "Conflict", ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, NativeWebRequest request) {
        LOG.error("Validation errors occurred", ex);
        BindingResult result = ex.getBindingResult();

        List<FieldError> fieldErrors = result.getFieldErrors().stream()
                .map(fe -> new FieldError(
                        extractField(fe.getField()),
                        fe.getDefaultMessage(),
                        fe.getObjectName(),
                        fe.getRejectedValue()
                ))
                .collect(Collectors.toList());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("ValidationError")
                .message("Request validation failed. Endpoint: " +
                    ((ServletWebRequest) request).getRequest().getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException cve, HttpServletRequest req) {
        LOG.error("Constraint violation", cve);
        Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
        List<FieldError> fieldErrors = new ArrayList<>();

        if (!violations.isEmpty()) {
            for (ConstraintViolation<?> violation : violations) {
                FieldError fieldError = new FieldError(
                        ((PathImpl) violation.getPropertyPath()).getLeafNode().getName(),
                        violation.getMessage(),
                        violation.getPropertyPath().toString(),
                        violation.getInvalidValue()
                );
                fieldErrors.add(fieldError);
            }
        }

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("ValidationError")
                .message("Request validation failed")
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {
        LOG.error("Invalid argument", ex);
        return new ResponseEntity<>(
            ApiResponse.error(400, "BadRequest", ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        LOG.error("Method not supported", ex);
        return new ResponseEntity<>(
            ApiResponse.error(405, "MethodNotAllowed", "HTTP method '" + ex.getMethod() + "' is not supported"),
            HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest req) {
        LOG.error("Unhandled exception occurred", ex);
        return new ResponseEntity<>(
            ApiResponse.error(500, "InternalServerError", ApiConstants.INTERNAL_ERROR_MESSAGE),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private String extractField(String field) {
        if (field != null && field.lastIndexOf(".") > 0) {
            return field.substring(field.lastIndexOf(".") + 1);
        }
        return field;
    }
}