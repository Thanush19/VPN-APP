package com.safetunnel.backend.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsUpStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/health"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.service").value("safetunnel-backend"));
    }

    @Test
    void livenessEndpointReturnsAliveStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/health/liveness"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ALIVE"));
    }

    @Test
    void readinessEndpointReturnsReadyStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/health/readiness"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("READY"));
    }
}