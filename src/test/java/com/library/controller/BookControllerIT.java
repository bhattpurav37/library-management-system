package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.BookRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerBook_thenAppearsInList() throws Exception {
        BookRequest request = new BookRequest("9780134685991", "Effective Java", "Joshua Bloch");

        mockMvc.perform(post("/api/v1/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.available").value(true));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title == 'Effective Java')]", hasSize(1)));
    }

    @Test
    void registerBook_rejectsBlankTitle() throws Exception {
        String badPayload = "{\"isbn\":\"9780134685991\",\"title\":\"\",\"author\":\"Joshua Bloch\"}";

        mockMvc.perform(post("/api/v1/books")
                        .contentType("application/json")
                        .content(badPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerBook_rejectsIsbnReusedForDifferentTitle() throws Exception {
        BookRequest first = new BookRequest("9781491950357", "Building Microservices", "Sam Newman");
        BookRequest conflicting = new BookRequest("9781491950357", "Some Other Book", "Sam Newman");

        mockMvc.perform(post("/api/v1/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/books")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(conflicting)))
                .andExpect(status().isConflict());
    }
}
