package com.library.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;

class BookCreatedEventKafkaConsumerTest {

    private final BookCreatedEventKafkaConsumer consumer = new BookCreatedEventKafkaConsumer();

    @Test
    void onBookCreated_processesEventWithoutError() {
        BookCreatedEvent event = new BookCreatedEvent(
                42L, "9780132350884", "Clean Code", "Robert C. Martin", LocalDateTime.now());

        assertThatCode(() -> consumer.onBookCreated(event)).doesNotThrowAnyException();
    }
}
