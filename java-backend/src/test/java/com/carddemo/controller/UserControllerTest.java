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
class UserControllerTest {

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
    void authenticate_success() throws Exception {
        mockMvc.perform(post("/api/v1/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"admin01\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void authenticate_failure() throws Exception {
        mockMvc.perform(post("/api/v1/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"admin01\", \"password\": \"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void listUsers_returnsAll() throws Exception {
        mockMvc.perform(get("/api/v1/users").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getUser_returnsById() throws Exception {
        mockMvc.perform(get("/api/v1/users/admin01").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("admin01"))
                .andExpect(jsonPath("$.userType").value("A"));
    }

    @Test
    void createUser_success() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"newusr01\", \"firstName\": \"New\", " +
                                "\"lastName\": \"User\", \"password\": \"pass1234\", " +
                                "\"userType\": \"U\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("newusr01"));
    }

    @Test
    void createUser_invalidType() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"baduser\", \"password\": \"pass\", " +
                                "\"userType\": \"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_success() throws Exception {
        mockMvc.perform(put("/api/v1/users/user0001")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }
}
