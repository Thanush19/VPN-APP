package com.safetunnel.backend.common.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `health endpoint returns UP status`() {
        mockMvc.perform(get("/api/v1/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("safetunnel-backend"))
    }

    @Test
    fun `liveness endpoint returns ALIVE status`() {
        mockMvc.perform(get("/api/v1/health/liveness"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("ALIVE"))
    }

    @Test
    fun `readiness endpoint returns READY status`() {
        mockMvc.perform(get("/api/v1/health/readiness"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("READY"))
    }
}