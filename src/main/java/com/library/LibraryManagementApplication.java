package com.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LibraryManagementApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        LOGGER.info(String.format("Library Management System is ready!"));
        LOGGER.info(String.format("Access the API at: http://localhost:8881/library"));
        LOGGER.info(String.format("API Endpoints:"));
        LOGGER.info(String.format("  - Books: GET http://localhost:8881/library/api/v1/books"));
        LOGGER.info(String.format("  - Books: GET http://localhost:8881/library/api/v1/books/{id}"));
        LOGGER.info(String.format("  - Books: POST http://localhost:8881/library/api/v1/books"));
        LOGGER.info(String.format("  - Borrowers: GET http://localhost:8881/library/api/v1/borrowers"));
        LOGGER.info(String.format("  - Borrowers: POST http://localhost:8881/library/api/v1/borrowers"));
        LOGGER.info(String.format("  - Borrow: POST http://localhost:8881/library/api/v1/books/{bookId}/borrow"));
        LOGGER.info(String.format("  - Return: POST http://localhost:8881/library/api/v1/books/{bookId}/return"));
    }

}
