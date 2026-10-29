package com.library.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookCreatedEventKafkaPublisherTest {

    private static final String TOPIC = "library.book-created.v1";

    @Mock
    private KafkaTemplate<String, BookCreatedEvent> kafkaTemplate;

    private BookCreatedEventKafkaPublisher publisher;

    @Test
    void onBookCreated_sendsEventKeyedByIsbn() {
        publisher = new BookCreatedEventKafkaPublisher(kafkaTemplate, TOPIC);
        given(kafkaTemplate.send(anyString(), anyString(), any(BookCreatedEvent.class)))
                .willReturn(new CompletableFuture<>());
        BookCreatedEvent event = new BookCreatedEvent(
                1L, "9780132350884", "Clean Code", "Robert C. Martin", LocalDateTime.now());

        publisher.onBookCreated(event);

        verify(kafkaTemplate).send(TOPIC, "9780132350884", event);
    }

    @Test
    void onBookCreated_swallowsSendFailure() {
        publisher = new BookCreatedEventKafkaPublisher(kafkaTemplate, TOPIC);
        CompletableFuture<SendResult<String, BookCreatedEvent>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("broker down"));
        given(kafkaTemplate.send(anyString(), anyString(), any(BookCreatedEvent.class)))
                .willReturn(failed);
        BookCreatedEvent event = new BookCreatedEvent(
                1L, "9780132350884", "Clean Code", "Robert C. Martin", LocalDateTime.now());

        // The book is already committed at this point - a failed notification must not propagate.
        assertThatCode(() -> publisher.onBookCreated(event)).doesNotThrowAnyException();
        verify(kafkaTemplate).send(eq(TOPIC), eq("9780132350884"), eq(event));
    }
}
