package com.library.constants;

/**
 * API Constants for Library Management System
 * Centralizes API path constants and the shared internal-error message.
 */
public final class ApiConstants {

    private ApiConstants() {
        throw new IllegalStateException("Constants class");
    }

    // API Base Paths
    public static final String API_BASE_PATH = "/api/v1";
    public static final String BOOKS_PATH = API_BASE_PATH + "/books";
    public static final String BORROWERS_PATH = API_BASE_PATH + "/borrowers";
    public static final String BORROW_PATH = API_BASE_PATH + "/books/{bookId}/borrow";
    public static final String RETURN_PATH = API_BASE_PATH + "/books/{bookId}/return";

    // Response Messages
    public static final String INTERNAL_ERROR_MESSAGE = "An unexpected error occurred";
}
