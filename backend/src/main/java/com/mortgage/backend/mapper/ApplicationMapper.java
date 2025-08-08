package com.mortgage.backend.mapper;

import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DocumentRequest;
import com.mortgage.backend.dto.DocumentResponse;
import com.mortgage.backend.model.Application;
import com.mortgage.backend.model.Document;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {
    Application toEntity(ApplicationRequest request);
    ApplicationResponse toDto(Application application);
    List<Document> mapDocuments(List<DocumentRequest> documentRequests);
    List<DocumentResponse> mapDocumentResponse(List<Document> documents);


    @AfterMapping
    default void setApplicationInDocuments(@MappingTarget Application application) {
        if (application.getDocuments() != null) {
            for (Document doc : application.getDocuments()) {
                doc.setApplication(application);
            }
        }
    }

    @AfterMapping
    default void setNationalId(@MappingTarget ApplicationResponse response, Application application) {
        if (application.getApplicant() != null) {
            response.setNationalId(application.getApplicant().getNationalId());
        }
    }
}
