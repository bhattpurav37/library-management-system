package com.library.event;

import java.time.LocalDateTime;

/**
 * Published after a book has been successfully committed to the database.
 * Carries only the data a downstream consumer needs - not the JPA entity,
 * so the persistence shape is not coupled to the wire contract.
 */
public record BookCreatedEvent(
        Long bookId,
        String isbn,
        String title,
        String author,
        LocalDateTime occurredAt) {
}
