package com.library.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Publishes BookCreatedEvent to Kafka only after the registering transaction has committed,
 * so no event is emitted for a book that gets rolled back.
 */
@Component
public class BookCreatedEventKafkaPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookCreatedEventKafkaPublisher.class);

    private final KafkaTemplate<String, BookCreatedEvent> kafkaTemplate;
    private final String topic;

    public BookCreatedEventKafkaPublisher(KafkaTemplate<String, BookCreatedEvent> kafkaTemplate,
                                          @Value("${app.kafka.topics.book-created}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookCreated(BookCreatedEvent event) {
        LOGGER.info(String.format("Publishing BookCreatedEvent for book id: [%s], isbn: [%s] to topic: [%s]",
                event.bookId(), event.isbn(), topic));
        // Keyed by ISBN so events for the same book land on the same partition and stay ordered.
        kafkaTemplate.send(topic, event.isbn(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // Swallowed on purpose: the book IS created (the commit already happened),
                        // so a failed notification must not affect the API response. The error is
                        // logged for alerting/replay. A production system would add a
                        // dead-letter/outbox retry here.
                        LOGGER.error(String.format("Failed to publish BookCreatedEvent for book id: [%s]", event.bookId()), ex);
                    }
                });
    }
}
