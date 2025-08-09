package com.mortgage.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mortgage.backend.config.StreamProducer;
import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DecisionRequestDto;
import com.mortgage.backend.dto.DocumentRequest;
import com.mortgage.backend.enums.Enum.ApplicationStatus;
import com.mortgage.backend.enums.Enum.DecisionType;
import com.mortgage.backend.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApplicationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationService  applicationService;

    @MockitoBean
    private StreamProducer streamProducer;

    @Test
    @WithMockUser(username = "testuser", roles = {"APPLICANT"})
    void whenCreateApplicationThenSuccess() throws Exception {
        Mockito.doNothing().when(streamProducer).produce(Mockito.any(), Mockito.any(), Mockito.any());

        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Buy new home");
        request.setAmount(100000.0);

        // Create documents
        DocumentRequest doc1 = new DocumentRequest();
        doc1.setFileName("ID Proof");
        doc1.setType("PDF");
        doc1.setSize(204800L);
        doc1.setPresignedUrl("https://example.com/signed-url-1");

        DocumentRequest doc2 = new DocumentRequest();
        doc2.setFileName("Income Statement");
        doc2.setType("PDF");
        doc2.setSize(307200L);
        doc2.setPresignedUrl("https://example.com/signed-url-2");

        // Add to request
        request.setDocuments(List.of(doc1, doc2));

        mockMvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.purpose").value("Buy new home"))
                .andExpect(jsonPath("$.nationalId").value(31328103L))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents.length()").value(2));
    }

    @Test
    @WithMockUser(username = "testofficer", roles = {"OFFICER"})
    void whenGetApplicationByIdThenReturnApplication() throws Exception {
        Mockito.doNothing().when(streamProducer).produce(Mockito.any(), Mockito.any(), Mockito.any());

        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Investment property");
        request.setAmount(100000.0);

        ApplicationResponse response = applicationService.create(request);

        mockMvc.perform(get("/api/v1/applications/" + response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.purpose").value("Investment property"));
    }

    @Test
    @WithMockUser(username = "testofficer", roles = {"OFFICER"})
    void whenSearchApplicationsAsOfficerThenReturnResults() throws Exception {
        Mockito.doNothing().when(streamProducer).produce(Mockito.any(), Mockito.any(), Mockito.any());

        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Build rentals");
        request.setAmount(100000.0);

        applicationService.create(request);

        mockMvc.perform(get("/api/v1/applications")
                        .param("purpose", "Build rentals")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "nathan_applicant", roles = {"APPLICANT"})
    void whenSearchApplicationsAsApplicantThenReturnResults() throws Exception {
        Mockito.doNothing().when(streamProducer).produce(Mockito.any(), Mockito.any(),  Mockito.any());

        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Build rentals");
        request.setAmount(100000.0);

        applicationService.create(request);

        // No need to pass nationalId — it will be resolved from the username in token
        mockMvc.perform(get("/api/v1/applications")
                        .param("purpose", "Build rentals")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "testofficer", roles = {"OFFICER"})
    void whenDecideOnApplicationThenSuccess() throws Exception {
        Mockito.doNothing().when(streamProducer).produce(Mockito.any(), Mockito.any(),  Mockito.any());

        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(31328103L);
        request.setPurpose("Build own residence");
        request.setAmount(90000.0);

        ApplicationResponse applicationResponse = applicationService.create(request);

        DecisionRequestDto decision = new DecisionRequestDto();
        decision.setApproverId(UUID.fromString("afc5d2ba-7ab7-4ad2-93b5-10ae3eda373f"));
        decision.setComments("Approved");
        decision.setStatus(ApplicationStatus.APPROVED);
        decision.setDecisionType(DecisionType.APPROVED);

        mockMvc.perform(patch("/api/v1/applications/" + applicationResponse.getId() + "/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
