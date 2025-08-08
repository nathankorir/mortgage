package com.mortgage.backend.mapper;

import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DocumentRequest;
import com.mortgage.backend.dto.DocumentResponse;
import com.mortgage.backend.enums.Enum;
import com.mortgage.backend.model.Application;
import com.mortgage.backend.model.Document;
import com.mortgage.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
public class ApplicationMapperTest {
    private ApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ApplicationMapper.class);
    }

    @Test
    void givenRequest_whenToEntity_thenMapsFieldsAndSetsApplicationInDocuments() {
        // given
        ApplicationRequest request = new ApplicationRequest();
        request.setNationalId(12345678L);
        request.setPurpose("Buy house");
        request.setAmount(250000.0);

        DocumentRequest docReq1 = new DocumentRequest();
        docReq1.setFileName("doc1.pdf");
        docReq1.setType("PDF");
        docReq1.setSize(1024L);
        docReq1.setPresignedUrl("http://url1");

        DocumentRequest docReq2 = new DocumentRequest();
        docReq2.setFileName("doc2.pdf");
        docReq2.setType("PDF");
        docReq2.setSize(2048L);
        docReq2.setPresignedUrl("http://url2");

        request.setDocuments(List.of(docReq1, docReq2));

        // when
        Application entity = mapper.toEntity(request);

        // then
        assertThat(entity.getPurpose()).isEqualTo("Buy house");
        assertThat(entity.getAmount()).isEqualTo(250000.0);
        assertThat(entity.getDocuments()).hasSize(2);
        entity.getDocuments().forEach(doc -> assertThat(doc.getApplication()).isEqualTo(entity));
    }

    @Test
    void givenEntity_whenToDto_thenMapsFieldsAndSetsNationalId() {
        // given
        Application application = new Application();
        application.setId(UUID.randomUUID());
        application.setPurpose("Investment");
        application.setAmount(500000.0);
        application.setStatus(Enum.ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());

        User applicant = new User();
        applicant.setNationalId(987654321L);
        application.setApplicant(applicant);

        Document doc1 = new Document();
        doc1.setFileName("passport.pdf");
        doc1.setType("PDF");
        doc1.setSize(5000L);
        doc1.setPresignedUrl("http://presigned1");

        application.setDocuments(List.of(doc1));

        // when
        ApplicationResponse dto = mapper.toDto(application);

        // then
        assertThat(dto.getId()).isEqualTo(application.getId());
        assertThat(dto.getPurpose()).isEqualTo("Investment");
        assertThat(dto.getNationalId()).isEqualTo(987654321L);
        assertThat(dto.getDocuments()).hasSize(1);
        assertThat(dto.getDocuments().get(0).getFileName()).isEqualTo("passport.pdf");
    }

    @Test
    void givenDocumentRequests_whenMapDocuments_thenMapsCorrectly() {
        DocumentRequest docReq = new DocumentRequest();
        docReq.setFileName("file.pdf");
        docReq.setType("PDF");
        docReq.setSize(1234L);
        docReq.setPresignedUrl("http://url");

        List<Document> documents = mapper.mapDocuments(List.of(docReq));

        assertThat(documents).hasSize(1);
        Document doc = documents.get(0);
        assertThat(doc.getFileName()).isEqualTo("file.pdf");
        assertThat(doc.getType()).isEqualTo("PDF");
        assertThat(doc.getSize()).isEqualTo(1234L);
        assertThat(doc.getPresignedUrl()).isEqualTo("http://url");
    }

    @Test
    void givenDocuments_whenMapDocumentResponse_thenMapsCorrectly() {
        Document doc = new Document();
        doc.setFileName("contract.pdf");
        doc.setType("PDF");
        doc.setSize(5555L);
        doc.setPresignedUrl("http://doc-url");

        List<DocumentResponse> responses = mapper.mapDocumentResponse(List.of(doc));

        assertThat(responses).hasSize(1);
        DocumentResponse response = responses.get(0);
        assertThat(response.getFileName()).isEqualTo("contract.pdf");
        assertThat(response.getType()).isEqualTo("PDF");
        assertThat(response.getSize()).isEqualTo(5555L);
        assertThat(response.getPresignedUrl()).isEqualTo("http://doc-url");
    }
}
