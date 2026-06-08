package com.carddemo.controller;

import com.carddemo.config.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    private String token;

    @BeforeEach
    void setUp() {
        token = "Bearer " + jwtUtils.generateToken("admin01", "A");
    }

    @Test
    void listTransactions_returnsPagedResults() throws Exception {
        mockMvc.perform(get("/api/v1/transactions").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getTransaction_returnsById() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/0000000000000001").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("0000000000000001"))
                .andExpect(jsonPath("$.amount").value(125.50));
    }

    @Test
    void createTransaction_success() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typeCode\": \"SA\", \"categoryCode\": 5001, " +
                                "\"source\": \"POS\", \"description\": \"Test purchase\", " +
                                "\"amount\": 50.00, \"cardNumber\": \"4111111111111111\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value("4111111111111111"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void createTransaction_invalidCard() throws Exception {
        mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typeCode\": \"SA\", \"amount\": 50.00, " +
                                "\"cardNumber\": \"9999999999999999\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByCard_returnsFilteredResults() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/by-card/4111111111111111").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
