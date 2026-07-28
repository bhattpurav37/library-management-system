package com.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration following standard SpringDoc patterns
 * Provides API documentation and Swagger UI
 */
@Configuration
public class OpenApiConfig {

    private static final String API_VERSION = "1.0.0";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management System REST API")
                        .description("""
                                REST API for managing borrowers, books and lending in a small library.

                                ## Base path
                                All endpoints are exposed under `/library/api/v1`, where `/library` is the
                                server servlet context path and `/api/v1` is the API version prefix.

                                ## Conventions
                                - **Resources** are identified by a numeric `id`. A book `id` identifies one
                                  physical copy, not the work (multiple copies share the same ISBN).
                                - **HTTP status codes** follow REST conventions: `201` for creates, `200` for
                                  reads and the return action, `400` for validation failures, `404` for
                                  missing resources, `409` for conflict states (double-borrow, ISBN
                                  conflict), `405` for unsupported methods, and `500` for unexpected errors.
                                - **Concurrency** on borrowing is handled with pessimistic row-level locking
                                  so two concurrent borrows of the same copy cannot both succeed.

                                ## Error format
                                Every error response uses a single consistent shape:
                                `timestamp`, `status`, `error`, `message`, and (for validation failures) a
                                `fieldErrors` array.
                                """)
                        .version("API Version: " + API_VERSION)
                        .contact(new Contact()
                                .name("Library Management System")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8881/library")
                                .description("Local dev server (H2)"),
                        new Server()
                                .url("http://localhost:8881/library")
                                .description("Docker Compose server (PostgreSQL, prod profile)")));
    }

    @Bean
    public GroupedOpenApi libraryApi() {
        return GroupedOpenApi.builder()
                .group("library-restful-api")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
