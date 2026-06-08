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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

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
    void listAccounts_returnsPagedResults() throws Exception {
        mockMvc.perform(get("/api/v1/accounts").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5));
    }

    @Test
    void getAccount_returnsAccountById() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/80001000001").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acctId").value(80001000001L))
                .andExpect(jsonPath("$.activeStatus").value("Y"))
                .andExpect(jsonPath("$.currentBalance").value(1500.00));
    }

    @Test
    void getAccount_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/99999999999").header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountDetails_returnsDetailedView() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/80001000001/details").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.acctId").value(80001000001L))
                .andExpect(jsonPath("$.cardXrefs").isArray())
                .andExpect(jsonPath("$.customer.firstName").value("John"));
    }

    @Test
    void updateAccount_validUpdate() throws Exception {
        mockMvc.perform(put("/api/v1/accounts/80003000003")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"creditLimit\": 5000.00, \"addressZip\": \"90211\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditLimit").value(5000.00))
                .andExpect(jsonPath("$.addressZip").value("90211"));
    }

    @Test
    void updateAccount_invalidStatus() throws Exception {
        mockMvc.perform(put("/api/v1/accounts/80001000001")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"activeStatus\": \"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAccount_success() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"acctId\": 90001000001, \"creditLimit\": 8000.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.acctId").value(90001000001L))
                .andExpect(jsonPath("$.activeStatus").value("Y"));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isUnauthorized());
    }
}
