package com.mortgage.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DecisionRequestDto;
import com.mortgage.backend.enums.Enum.ApplicationStatus;
import com.mortgage.backend.enums.Enum.DecisionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser")
public class ApplicationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateApplicationThenSuccess() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Buy new home");
        request.setAmount(100000.0);

        mockMvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.purpose").value("Buy new home"));
    }

    @Test
    void whenGetApplicationByIdThenReturnApplication() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Investment property");
        request.setAmount(100000.0);

        String response = mockMvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ApplicationResponse created = objectMapper.readValue(response, ApplicationResponse.class);

        mockMvc.perform(get("/api/v1/applications/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.purpose").value("Investment property"));
    }

    @Test
    void whenSearchApplicationsThenReturnResults() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Build rentals");
        request.setAmount(100000.0);

        mockMvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/applications")
                        .param("purpose", "Build rentals")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void whenDecideOnApplicationThenSuccess() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Build own residence");
        request.setAmount(90000.0);

        String response = mockMvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ApplicationResponse created = objectMapper.readValue(response, ApplicationResponse.class);

        DecisionRequestDto decision = new DecisionRequestDto();
        decision.setApproverId(UUID.fromString("afc5d2ba-7ab7-4ad2-93b5-10ae3eda373f"));
        decision.setComments("Approved");
        decision.setStatus(ApplicationStatus.APPROVED);
        decision.setDecisionType(DecisionType.APPROVED);

        mockMvc.perform(patch("/api/v1/applications/" + created.getId() + "/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
