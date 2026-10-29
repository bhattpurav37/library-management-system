package com.library.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes BookCreatedEvent from the book-created topic. Currently just logs each event -
 * the seam a downstream reaction (search indexing, notifications, analytics) would plug into.
 */
@Component
public class BookCreatedEventKafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookCreatedEventKafkaConsumer.class);

    @KafkaListener(id = "book-created", topics = "${app.kafka.topics.book-created}",
            containerFactory = "bookEventListenerContainerFactory")
    public void onBookCreated(BookCreatedEvent event) {
        LOGGER.info(String.format("Received BookCreatedEvent for book id: [%s], isbn: [%s], title: [%s]",
                event.bookId(), event.isbn(), event.title()));
    }
}
