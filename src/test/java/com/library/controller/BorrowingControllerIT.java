package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.BookRequest;
import com.library.dto.BorrowerRequest;
import com.library.event.BookCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BorrowingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityTestSupport security;

    // Replaces the real template so no Kafka broker is needed (books are registered here as setup).
    @MockBean
    private KafkaTemplate<String, BookCreatedEvent> kafkaTemplate;

    @Test
    void secondBorrowerCannotBorrowBookThatIsAlreadyOut() throws Exception {
        Long bookId = registerBook("9780596007126", "Head First Design Patterns", "Eric Freeman");
        Long borrower1 = registerBorrower("Grace Hopper", "grace@example.com");
        Long borrower2 = registerBorrower("Alan Turing", "alan@example.com");

        String borrowBody1 = objectMapper.writeValueAsString(new java.util.HashMap<>() {{
            put("borrowerId", borrower1);
        }});
        mockMvc.perform(post("/api/v1/books/" + bookId + "/borrow")
                        .header("Authorization", security.borrowerBearer())
                        .contentType("application/json")
                        .content(borrowBody1))
                .andExpect(status().isCreated());

        String borrowBody2 = objectMapper.writeValueAsString(new java.util.HashMap<>() {{
            put("borrowerId", borrower2);
        }});
        mockMvc.perform(post("/api/v1/books/" + bookId + "/borrow")
                        .header("Authorization", security.borrowerBearer())
                        .contentType("application/json")
                        .content(borrowBody2))
                .andExpect(status().isConflict());

        // returning it should free it up again
        mockMvc.perform(post("/api/v1/books/" + bookId + "/return")
                        .header("Authorization", security.borrowerBearer())
                        .contentType("application/json")
                        .content(borrowBody1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/books/" + bookId + "/borrow")
                        .header("Authorization", security.borrowerBearer())
                        .contentType("application/json")
                        .content(borrowBody2))
                .andExpect(status().isCreated());
    }

    private Long registerBook(String isbn, String title, String author) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/books")
                        .header("Authorization", security.librarianBearer())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new BookRequest(isbn, title, author))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long registerBorrower(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/borrowers")
                        .header("Authorization", security.librarianBearer())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new BorrowerRequest(name, email))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
