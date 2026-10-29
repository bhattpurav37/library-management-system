package com.library.config;

import com.library.event.BookCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Typed Kafka producer for domain events. A dedicated template (instead of relying on
 * KafkaAutoConfiguration's KafkaTemplate&lt;Object, Object&gt;) wires the app's Spring ObjectMapper
 * into the JsonSerializer, so jackson settings like non_null inclusion apply to events too,
 * and disables Spring's type-info headers so consumers do not need the producer's classes.
 */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, BookCreatedEvent> bookEventProducerFactory(Environment env,
                                                                             ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, env.getProperty("spring.kafka.producer.acks", "all"));
        props.put(ProducerConfig.RETRIES_CONFIG,
                env.getProperty("spring.kafka.producer.retries", Integer.class, 10));

        JsonSerializer<BookCreatedEvent> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        DefaultKafkaProducerFactory<String, BookCreatedEvent> factory =
                new DefaultKafkaProducerFactory<>(props);
        factory.setValueSerializer(jsonSerializer);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, BookCreatedEvent> bookEventKafkaTemplate(
            ProducerFactory<String, BookCreatedEvent> bookEventProducerFactory) {
        return new KafkaTemplate<>(bookEventProducerFactory);
    }
}
